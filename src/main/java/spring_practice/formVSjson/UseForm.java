package spring_practice.formVSjson;

import lombok.Getter;


@Getter
public class UseForm {
    private String name;
    private int age;

    public void setName(String name){
        this.name = name;
    }

    public void setAge(int age){
        this.age = age;
    }

}
