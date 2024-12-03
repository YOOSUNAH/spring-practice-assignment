package spring_practice.demo.file;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class FileUploadRequest {

    private MultipartFile file;

    private String description;


    public void setFile(MultipartFile file){
        this.file = file;
    }

    public void setDescription(String description){
        this.description = description;
    }

}
