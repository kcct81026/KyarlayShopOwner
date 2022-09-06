package com.techmyanmar.kcct.kyarlaysupplier.operation;

public interface Constant {

    //static String basicURL = "http://159.223.58.13";
   static String basicURL = "https://kyarlay.com";

    static String constLoginUrl = basicURL +  "/suppliers/api/login?";
    static String constCategoryList = basicURL +  "/suppliers/api/categories";
    static String constSupplierCategoryList = basicURL +  "/suppliers/api/categories/list";
    static String constCreateProduct = basicURL +  "/suppliers/api/products";
    static String constSearch = basicURL + "/suppliers/api/products/search?";
    static String constOtpGetCode = basicURL + "/suppliers/api/otp/request";
    static String constOtpVerifyCode = basicURL + "/suppliers/api/otp/verify";
    static String constChangePasword = basicURL + "/suppliers/api/password/reset";
    static String constDelete = basicURL + "/suppliers/api/products/delete";
    static String constEnable= basicURL + "/suppliers/api/products/enable";
    static String constDisable= basicURL + "/suppliers/api/products/disable";
    static String constantUpdateFcmID = basicURL + "/suppliers/api/products/disable";

}
