package com.techmyanmar.kcct.kyarlaysupplier.VO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ProductList extends UniversalPost implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("reviewer_status")
    private String reviewer_status;

    @SerializedName("sku")
    private String sku;

    @SerializedName("title")
    private String title;

    @SerializedName("desc")
    private String desc;

    @SerializedName("category_id")
    private int category_id;

    @SerializedName("price")
    private int price;

    @SerializedName("supplier_price")
    private int supplier_price;

    @SerializedName("supplier_id")
    private int supplier_id;

    @SerializedName("channel")
    private int channel;

    @SerializedName("member_discount")
    private int member_discount;

    @SerializedName("point_usage")
    private  int point_usage;

    @SerializedName("status")
    private String status;

    @SerializedName("photos")
    private String[] photos;

    public ProductList(){}

    protected ProductList(Parcel in) {
        id = in.readInt();
        reviewer_status = in.readString();
        sku = in.readString();
        title = in.readString();
        desc = in.readString();
        category_id = in.readInt();
        price = in.readInt();
        supplier_price = in.readInt();
        supplier_id = in.readInt();
        channel = in.readInt();
        member_discount = in.readInt();
        point_usage = in.readInt();
        status = in.readString();
        photos = in.createStringArray();
    }

    public static final Creator<ProductList> CREATOR = new Creator<ProductList>() {
        @Override
        public ProductList createFromParcel(Parcel in) {
            return new ProductList(in);
        }

        @Override
        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReviewer_status() {
        return reviewer_status;
    }

    public void setReviewer_status(String reviewer_status) {
        this.reviewer_status = reviewer_status;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSupplier_price() {
        return supplier_price;
    }

    public void setSupplier_price(int supplier_price) {
        this.supplier_price = supplier_price;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getMember_discount() {
        return member_discount;
    }

    public void setMember_discount(int member_discount) {
        this.member_discount = member_discount;
    }

    public int getPoint_usage() {
        return point_usage;
    }

    public void setPoint_usage(int point_usage) {
        this.point_usage = point_usage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(reviewer_status);
        parcel.writeString(sku);
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeInt(category_id);
        parcel.writeInt(price);
        parcel.writeInt(supplier_price);
        parcel.writeInt(supplier_id);
        parcel.writeInt(channel);
        parcel.writeInt(member_discount);
        parcel.writeInt(point_usage);
        parcel.writeString(status);
        parcel.writeStringArray(photos);
    }
}

