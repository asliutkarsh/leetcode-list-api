package com.leetcodeapi.exception;

public class ResourceNotFoundException extends RuntimeException{

    String resourceName;
    String fieldName;
    Long field;

    String strField;

    public ResourceNotFoundException(String resourceName,String fieldName, Long field){
        super(String.format("%s not found with %s : %s",resourceName,fieldName,field));
        this.resourceName=resourceName;
        this.fieldName=fieldName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String strField) {
        super(String.format("%s not found with %s : %s",resourceName,fieldName,strField));
        this.resourceName=resourceName;
        this.fieldName=fieldName;
        this.strField = strField;

    }
}
