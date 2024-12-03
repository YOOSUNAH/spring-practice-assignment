package spring_practice.demo.file;

import lombok.Getter;

@Getter
public class RequestPartDto {

    /** 올린사람 */
    private String postPerson;

    /** 파일명 **/
    private String fileName;

    public void setPostPerson(String postPerson){
        this.postPerson = postPerson;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }
}
