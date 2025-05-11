package com.javanostra.meetyourmatch.persistance.entity;

import java.util.Objects;

/**
 * Data Transfer Object для ответа сервера после успешной загрузки файла.
 * Эквивалент com.javanostra.spring.core.dto.FileUploadedDTO.
 *
 * Примечание: В серверном DTO поле называется 'fileId',
 * но в конструкторе используется 'objectId'. В клиентском DTO
 * будем использовать 'fileId', как в аннотации @Data.
 * Убедитесь, что Gson на клиенте корректно смаппит поле 'objectId' из JSON
 * в поле 'fileId' этого класса, если имена в JSON и классе отличаются.
 * Если имена в JSON совпадают с полями этого класса, проблем быть не должно.
 */
public class FileUploadedDTO {

    private String fileId; 
    private String path;

    
    public FileUploadedDTO() {
    }

    
    public FileUploadedDTO(String fileId, String path) {
        this.fileId = fileId;
        this.path = path;
    }

    

    public String getFileId() {
        return fileId;
    }

    public String getPath() {
        return path;
    }

    

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    

    @Override
    public String toString() {
        return "FileUploadedDTO{" +
                "fileId='" + fileId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileUploadedDTO that = (FileUploadedDTO) o;
        return Objects.equals(fileId, that.fileId) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, path);
    }
}