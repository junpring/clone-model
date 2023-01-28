package io.junpring.coupang.controllers;

import io.junpring.coupang.dtos.product.ProductDTO;
import io.junpring.coupang.services.ProductService;
import io.junpring.coupang.vos.product.ProductPageVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(value = "/")
public class RootController {
    private final ProductService productService;

    public RootController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getIndex(
            ModelAndView modelAndView,
            ProductPageVo productPageVo,
            @RequestParam(value = "category", required = false) Optional<Integer> optionalCategory,
            @RequestParam(value = "page", defaultValue = "1", required = true) int page
    ) {
        int category = optionalCategory.orElse(0);

        productPageVo.setRequestPage(page);

        ProductDTO[] productDtos = this.productService.getProducts(productPageVo);

        if (category > 0) {
            productDtos = Arrays.stream(productDtos).filter(x -> x.getCategoryIndex() == category).toArray(ProductDTO[]::new);
        }

        modelAndView.addObject("productPageVo", productPageVo);

        modelAndView.addObject("productDtos", productDtos);
        modelAndView.addObject("categoryEntities", this.productService.getCategories());
        modelAndView.setViewName("root/index");
        return modelAndView;
    }


}
