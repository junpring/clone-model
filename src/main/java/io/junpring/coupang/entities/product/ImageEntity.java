package io.junpring.coupang.entities.product;

import javax.xml.crypto.Data;
import java.util.Date;

public class ImageEntity {
    private String id; // 이미지 아이디
    private Date createAt; // 이미지 생성시간
    private String fileName; // 파일 이름
    private String fileType; // 파일타입 (jpeg, png)
    private byte[] data; // 이미지 크기, 사이즈, 확장자 등

    public ImageEntity() {
    }

    public ImageEntity(String id, Date createAt, String fileName, String fileType, byte[] data) {
        this.id = id;
        this.createAt = createAt;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
