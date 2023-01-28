package io.junpring.coupang.services;


import io.junpring.coupang.dtos.product.ProductDTO;
import io.junpring.coupang.entities.product.*;
import io.junpring.coupang.enums.product.AddResult;
import io.junpring.coupang.enums.product.DetailResult;
import io.junpring.coupang.mappers.IProductMapper;
import io.junpring.coupang.utils.CryptoUtil;
import io.junpring.coupang.vos.product.AddVo;
import io.junpring.coupang.vos.product.DetailVo;
import io.junpring.coupang.vos.product.ProductPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    public static final int[] PRODUCT_PER_PAGE = new int[]{12, 22};
    //게시판에 페이지당 보여질 상품 글 개수
    private final IProductMapper productMapper;

    @Autowired
    public ProductService(IProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public void addProduct(AddVo addVo) {
        if (this.productMapper.insertProduct(addVo) == 0) {
            addVo.setResult(AddResult.FAILURE); // insertProduct 파라미터값이 0일때
        } else {
            addVo.setResult(AddResult.SUCCESS); // 성공
        }
    }

    @Transactional
    public void deleteThumbnail(ProductEntity productEntity, ThumbnailEntity thumbnailEntity) {
        this.productMapper.deleteThumbnail(thumbnailEntity);
        productEntity = this.productMapper.selectProductByIndex(productEntity.getIndex());
        // 일단 select로 긁어와서
        productEntity.setThumbnailId(null); // 직접 지정
        this.productMapper.updateProduct(productEntity); // null로 돌아감
    }

    public void deleteProduct(ProductEntity productEntity) {
        this.productMapper.deleteProduct(productEntity);
    }

    public CategoryEntity[] getCategories() {
        return this.productMapper.selectCategories();
    }

    public ImageEntity getImage(String id) { // 반환타입이 ImageEntity 객체
        return this.productMapper.selectImage(id); // 컨트롤러에한테 책임전가
    }

    public ThumbnailEntity getThumbnail(String id) {
        return this.productMapper.selectThumbnail(id);
    }



    public void getProduct(DetailVo detailVo) {
        ProductEntity productEntity = this.productMapper.selectProductByIndex(detailVo.getIndex());
        if (productEntity == null || productEntity.getTitle() == null) {
            detailVo.setResult(DetailResult.NOT_FOUND);
            return;
        }
        detailVo.setIndex(productEntity.getIndex());
        detailVo.setTitle(productEntity.getTitle());
        detailVo.setPrice(productEntity.getPrice());
        detailVo.setDelivery(productEntity.getDelivery());
        detailVo.setContent(productEntity.getContent());
        detailVo.setThumbnailId(productEntity.getThumbnailId());
        detailVo.setResult(DetailResult.SUCCESS);
    }

    public void getProduct(ProductDTO productDto) {
        ProductEntity productEntity = this.productMapper.selectProductByIndex(productDto.getIndex());
        if (productEntity == null || productEntity.getTitle() == null) {
            return;
        }
        productDto.setIndex(productEntity.getIndex());
        productDto.setTitle(productEntity.getTitle());
        productDto.setPrice(productEntity.getPrice());
        productDto.setDelivery(productEntity.getDelivery());
        productDto.setContent(productEntity.getContent());
        productDto.setThumbnailId(productEntity.getThumbnailId());
        productDto.setCount(this.productMapper.selectProductStockCount(productDto));
    }
    public ProductEntity getProductByIndex(int index) {
        return this.productMapper.selectProductByIndex(index);
    }

    public ProductDTO[] getProducts(ProductPageVo productPageVo) {
        final int pageDivFactor = 9; // 페이지 분할 계수 ex) 5이면 페이징 <<<1,2,3,4,5>> 까지 보여줌.
        int productCountTotal = this.productMapper.selectProductCountTotal(productPageVo);

        if (Arrays.stream(PRODUCT_PER_PAGE).noneMatch(x -> x == productPageVo.getProductPerPage())) {
            productPageVo.setProductPerPage(PRODUCT_PER_PAGE[0]);
        }
        productPageVo.setMinPage(1); // 최소페이지
        productPageVo.setMaxPage(productCountTotal / productPageVo.getProductPerPage() + (productCountTotal % productPageVo.getProductPerPage() == 0 ? 0 : 1)); // 최대 페이지 17
        if (productPageVo.getRequestPage() < productPageVo.getMinPage()) {
            productPageVo.setRequestPage(productPageVo.getMinPage());
        }
        if (productPageVo.getRequestPage() > productPageVo.getMaxPage()) {
            productPageVo.setRequestPage(productPageVo.getMaxPage());
        }
        productPageVo.setStartPage(((productPageVo.getRequestPage() - 1) / pageDivFactor) * pageDivFactor + 1);
        productPageVo.setEndPage(Math.min(productPageVo.getMaxPage(), productPageVo.getStartPage() + pageDivFactor - 1));
        ///

        return this.productMapper.selectProducts(productPageVo.getProductPerPage(), (productPageVo.getRequestPage() - 1) * productPageVo.getProductPerPage());
    }

    public void modifyProduct(ProductEntity productEntity) {
        this.productMapper.updateProduct(productEntity);
    }

    public void putThumbnail(int productIndex, MultipartFile multipartFile) throws IOException {
        String id = String.format("%s%d%f%f",
                new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()),
                productIndex,
                Math.random(),
                Math.random());
        id = CryptoUtil.hashSha512(id);
        ThumbnailEntity thumbnailEntity = new ThumbnailEntity(id, multipartFile.getBytes());
        this.productMapper.insertThumbnail(thumbnailEntity);

        ProductEntity productEntity = this.productMapper.selectProductByIndex(productIndex);
        productEntity.setThumbnailId(id);
        this.productMapper.updateProduct(productEntity);
    }

    //  업로드한 이미지를 가져옴
    public void uploadImages(ImageEntity... imageEntities) {
        for (ImageEntity imageEntity : imageEntities) {
            // 배열임 forEach ImageEntity가 imageEntities 될때까지
            this.productMapper.insertImage(imageEntity); // insertImage
        }
    }

    public StockEntity[] getStocks(ProductDTO productDTO, int limit) {
        return this.productMapper.selectStocks(productDTO.getIndex(), limit);
    }

    public void putStock(StockEntity stockEntity) {
        this.productMapper.insertStock(stockEntity);
    }
    //////////////////////////////////////////////

    public void searchProduct(ProductPageVo productPageVo) {
//        final int pageDivFactor = 9;
//        int productCountTotal;
        this.productMapper.selectProductCountLikeAll(productPageVo.getKeyword());

    }
}