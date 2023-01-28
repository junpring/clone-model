package io.junpring.coupang.controllers;

import io.junpring.coupang.dtos.product.ProductDTO;
import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.entities.product.ImageEntity;
import io.junpring.coupang.entities.product.ProductEntity;
import io.junpring.coupang.entities.product.StockEntity;
import io.junpring.coupang.entities.product.ThumbnailEntity;
import io.junpring.coupang.entities.shopping.ReviewEntity;
import io.junpring.coupang.enums.product.AddResult;
import io.junpring.coupang.services.ProductService;
import io.junpring.coupang.services.ShoppingService;
import io.junpring.coupang.utils.CryptoUtil;
import io.junpring.coupang.vos.product.AddVo;
import io.junpring.coupang.vos.product.DetailVo;
import io.junpring.coupang.vos.product.ProductPageVo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
    private final ProductService productService;
    private final ShoppingService shoppingService;

    @Autowired
    public ProductController(ProductService productService, ShoppingService shoppingService) {
        this.productService = productService;
        this.shoppingService = shoppingService;
    }




    //  상품 등록 = 상품 등록 페이지를 보여주기 위한 GET 방식
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public ModelAndView getAdd(
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity, // @RequestAttribute : request 에 설정 된 속성값 호출
            HttpServletResponse response, // 응답을 돌려주기 위한 HttpServletResponse
            ModelAndView modelAndView
    ) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404); // userEntity = null 이거나 isAdmin == 1 (관리자)가 아니면 404페이지로 응답을 돌려줌.
            return null;
        }
        modelAndView.addObject("categoryEntities", this.productService.getCategories());
        modelAndView.setViewName("product/add"); // 리턴할 html
        return modelAndView;
    }

    //  상품 등록 = 상품 등록을 위한 POST 방식
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ModelAndView postAdd(
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            HttpServletResponse response,
            AddVo addVo,
            ModelAndView modelAndView) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        } // get 방식과 동일
        addVo.setResult(null);
        this.productService.addProduct(addVo); // insertProduct
        if (addVo.getResult() == AddResult.SUCCESS) {
            modelAndView.setViewName("redirect:/product/detail/" + addVo.getIndex());
            // insertProduct 성공일 경우 redirect:/product/detail/ + insert된 인덱스번호로 리턴
        } else {
            modelAndView.addObject("addVo", addVo);
            modelAndView.setViewName("product/add"); // insertProduct 성공이 아닌경우
        }
        return modelAndView;
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public ModelAndView getDelete(
            HttpServletResponse response,
            ModelAndView modelAndView,
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "pid", required = true) int productIndex
    ) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        ProductEntity productEntity = new ProductEntity();
        productEntity.setIndex(productIndex);
        this.productService.deleteProduct(productEntity);
        System.out.println("productIndex : " + productIndex);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @RequestMapping(value = "download-image", method = RequestMethod.GET)
    @ResponseBody // 서버에서 클라이언트로 응답 데이터를 전송하기 위해 자바 객체를 HTTP 응답 본문의 객체로 변환하여 클라이언트로 전송
    public ResponseEntity<byte[]> getDownloadImage(
            // ResponseEntity : 사용자의 HttpRequest에 대한 응답 데이터를 포함하는 클래스
            @RequestParam(name = "id", required = false) String id
    ) { // ex) ?id=test
        ImageEntity imageEntity = null;
        if (id != null) {
            imageEntity = this.productService.getImage(id);
        }
        byte[] data = null;
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.NOT_FOUND; // 404 담음
        if (imageEntity != null && imageEntity.getData() != null) {
            data = imageEntity.getData();
            headers.add("Content-Type", imageEntity.getFileType());
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", imageEntity.getFileName()));
            headers.add("Content-Length", String.valueOf(data.length));
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(data, headers, status);
    }

    @RequestMapping(value = "add/upload-image", method = RequestMethod.POST)
    @ResponseBody
    public String postAddUploadImage(
            HttpServletResponse response,
            @RequestAttribute(name = "userEntity", required = false) UserEntity userEntity, // 관리자만 이용할 수 있게 하기 위해서, request 에 설정 된 속성값 호출
            @RequestParam(name = "upload") MultipartFile[] images)
        // 파라미터를 MultipartFile[]로 받는다, upload?images=
            throws IOException {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404); // 유저가 관리자가 아니면 404
            return null;
        }
        ImageEntity[] imageEntities = new ImageEntity[images.length]; // MultipartFile[] images.length를 ImageEntity[]로 받는다
        for (int i = 0; i < images.length; i++) { // 배열이니 for문
            MultipartFile image = images[i]; // MultipartFile image로 생성
            Date createdAt = new Date(); // 현재시간 생성
            String id = String.format("%s%s%f",
                    new SimpleDateFormat("yyyyMMddHHmmssSSS").format(createdAt), // 날짜 시간 분 초 등
                    image.getOriginalFilename(), // 이미지의 파일이름과 확장자
                    Math.random());
            id = CryptoUtil.hashSha512(id); // id 해싱
            imageEntities[i] = new ImageEntity(id, createdAt,
                    image.getOriginalFilename(),
                    image.getContentType(),
                    image.getBytes());
            // 반복문 돌린 값을 ImageEntity로 객체화 imageEntities[i]에 담음
        }
        this.productService.uploadImages(imageEntities); // insertImage
        JSONObject responseJson = new JSONObject(); // JSONObject 객체 생성
        JSONArray urlJson = new JSONArray(); // JSONArray 객체 생성
        for (ImageEntity imageEntity : imageEntities) {
            urlJson.put(String.format("http://127.0.0.1:8080/product/download-image?id=%s", imageEntity.getId()));
        }
        responseJson.put("url", urlJson);
        return responseJson.toString(); // Json 리턴법 업로드 성공했다는 응답을 돌려주기위함.
    }

    @RequestMapping(value = "detail/{pid}", method = RequestMethod.GET)
    public ModelAndView getDetail(
            ModelAndView modelAndView,
            ProductDTO productDto,
            @PathVariable(name = "pid", required = true) int productIndex) {
        productDto.setIndex(productIndex);
        this.productService.getProduct(productDto);

        double reviewRate = 00;
        ReviewEntity[] reviewEntities = this.shoppingService.getReviews(productDto);
        for (ReviewEntity reviewEntity: reviewEntities) {
            reviewRate += reviewEntity.getRate();
        }

        reviewRate /= reviewEntities.length;
        productDto.setReviewCount(reviewEntities.length);
        productDto.setReviewRate(reviewRate);


        modelAndView.addObject("productDto", productDto);
        modelAndView.addObject("stockEntites", this.productService.getStocks(productDto, 5));
        modelAndView.addObject("reviewEntities", reviewEntities);
        modelAndView.setViewName("product/detail");
        return modelAndView;
    }

    @RequestMapping(value = "detail/thumbnail", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDetailThumbnail(
            @RequestParam(value = "id", required = true) String id) {
        ThumbnailEntity thumbnailEntity = null;
        if (id != null) {
            thumbnailEntity = this.productService.getThumbnail(id);
        }
        byte[] data = null;
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        if (thumbnailEntity != null && thumbnailEntity.getData() != null) {
            data = thumbnailEntity.getData();
            headers.add("Content-Type", "image/png");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", id + ".png"));
            headers.add("Content-Length", String.valueOf(data.length));
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(data, headers, status);
    }

    @RequestMapping(value = "detail/thumbnail/add", method = RequestMethod.POST)
    public ModelAndView postDetailThumbnailAdd(
            HttpServletResponse response,
            ModelAndView modelAndView,
            @RequestAttribute(name = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "productIndex", required = true) int productIndex,
            @RequestParam(value = "thumbnail", required = true) MultipartFile thumbnail) throws IOException {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        this.productService.putThumbnail(productIndex, thumbnail);
        modelAndView.setViewName("redirect:/product/detail/" + productIndex);

        System.out.println(thumbnail.getOriginalFilename());

        return modelAndView;
    }

    @RequestMapping(value = "detail/thumbnail/delete", method = RequestMethod.GET)
    public ModelAndView getDetailThumbnailDelete(
            HttpServletResponse response,
            ModelAndView modelAndView,
            @RequestAttribute(name = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "id", required = true) String thumbnailId,
            @RequestParam(value = "pid", required = true) int productIndex
    ) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        ProductEntity productEntity = new ProductEntity();
        ThumbnailEntity thumbnailEntity = new ThumbnailEntity();
        productEntity.setIndex(productIndex);
        thumbnailEntity.setId(thumbnailId);
        this.productService.deleteThumbnail(productEntity, thumbnailEntity);
        modelAndView.setViewName("redirect:/product/detail/" + productIndex);
        return modelAndView;
    }

    @RequestMapping(value = "modify", method = RequestMethod.GET)
    public ModelAndView getModify(
            HttpServletResponse response,
            ModelAndView modelAndView,
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "pid", required = true) int productIndex
    ) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setIndex(productIndex);
        this.productService.getProduct(detailVo);
        modelAndView.addObject("categoryEntities", this.productService.getCategories());
        modelAndView.addObject("detailVo", detailVo);
        modelAndView.setViewName("product/modify");
        return modelAndView;
    }

    @RequestMapping(value = "modify", method = RequestMethod.POST)
    public ModelAndView postModify(
            HttpServletResponse response,
            ModelAndView modelAndView,
            ProductEntity productEntity,
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "pid", required = true) int productIndex
    ) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        productEntity.setIndex(productIndex);
        this.productService.modifyProduct(productEntity);
        System.out.println("getThumbnailId : " + productEntity.getThumbnailId());
        modelAndView.setViewName("redirect:/product/detail/" + productIndex);
        return modelAndView;
    }

    @RequestMapping(value = "stock/add", method = RequestMethod.POST)
    public ModelAndView postStockAdd(
            ModelAndView modelAndView,
            HttpServletResponse response,
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "productIndex", required = true) int productIndex,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "count", required = true) int count

    ) {
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        StockEntity stockEntity = new StockEntity();
        stockEntity.setProductIndex(productIndex);
        stockEntity.setCreatedAt(new Date());
        stockEntity.setCount(type.equals("out") ? count * -1 : count);
        this.productService.putStock(stockEntity);
        modelAndView.setViewName("redirect:/product/detail/" + productIndex);
        return modelAndView;
    }
    /////////////////////////////////////////////////////////////
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView getSearch(
            ModelAndView modelAndView,
            ProductPageVo productPageVo,
            @RequestParam(value = "page", defaultValue = "1", required = true) int page) {
        productPageVo.setRequestPage(page);
        this.productService.searchProduct(productPageVo);
        System.out.println("keyword : " + productPageVo.getKeyword());
        modelAndView.addObject("productPageVo", productPageVo);
        modelAndView.setViewName("product/search");
        return modelAndView;
    }
}


