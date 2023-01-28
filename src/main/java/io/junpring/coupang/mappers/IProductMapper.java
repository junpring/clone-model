package io.junpring.coupang.mappers;

import io.junpring.coupang.dtos.product.ProductDTO;
import io.junpring.coupang.entities.product.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IProductMapper {

    int deleteProduct(ProductEntity productEntity);

    int deleteThumbnail(ThumbnailEntity thumbnailEntity);

    int insertImage(ImageEntity imageEntity);

    int insertProduct(ProductEntity productEntity);

    int insertStock(StockEntity stockEntity);

    int insertThumbnail(ThumbnailEntity thumbnailEntity);

    CategoryEntity[] selectCategories();

    ImageEntity selectImage( // ImageEntity 반환타입
                             @Param(value = "id") String id);

    ThumbnailEntity selectThumbnail(
            @Param(value = "id") String id);

    ProductEntity selectProductByIndex(
            @Param(value = "index") int index);

    int selectProductStockCount(ProductEntity productEntity);

    ProductDTO[] selectProducts(
            @Param(value = "limit") int limit,
            @Param(value = "offset") int offset);

    StockEntity[] selectStocks(
            @Param(value = "productIndex") int productIndex,
            @Param(value = "limit") int limit);

    int updateProduct(ProductEntity productEntity);


    /////////////////////////페이징/////////////////////////////
    int selectProductCountTotal(ProductEntity productEntity);

    /////////////////////////search/////////////////////////////

    int selectProductCountLikeAll(
            @Param(value = "keyword") String keyword);
}
