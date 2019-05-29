package com.semisky.multimedia.aidl.photo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片实体类
 * 
 * @author liuyong
 * 
 */
public class PhotoInfo implements Parcelable {
	private int _id;// 自增长id
	private int usbFlag;// U盘标识
	private int fileType;// 文件类型
	private String fileUrl;// 文件路径
	private String fileFolder;// 文件所属文件夹
	private String fileName;// 文件名字
	private String fileNamePinYin;// 文件名字拼音

	public PhotoInfo() {
		super();
	}

	public PhotoInfo(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getUsbFlag() {
		return usbFlag;
	}

	public void setUsbFlag(int usbFlag) {
		this.usbFlag = usbFlag;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileFolder() {
		return fileFolder;
	}

	public void setFileFolder(String fileFolder) {
		this.fileFolder = fileFolder;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileNamePinYin() {
		return fileNamePinYin;
	}

	public void setFileNamePinYin(String fileNamePinYin) {
		this.fileNamePinYin = fileNamePinYin;
	}

	

	@Override
	public String toString() {
		return "PhotoInfo [_id=" + _id + ", usbFlag=" + usbFlag + ", fileType=" + fileType + ", fileUrl=" + fileUrl
				+ ", fileFolder=" + fileFolder + ", fileName=" + fileName + ", fileNamePinYin=" + fileNamePinYin + "]";
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeInt(this.usbFlag);
		dest.writeInt(this.fileType);
		dest.writeString(this.fileUrl);
		dest.writeString(this.fileFolder);
		dest.writeString(this.fileName);
		dest.writeString(this.fileNamePinYin);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {

		@Override
		public PhotoInfo createFromParcel(Parcel source) {
			PhotoInfo photoInfo = new PhotoInfo();
			photoInfo._id = source.readInt();
			photoInfo.usbFlag = source.readInt();
			photoInfo.fileType = source.readInt();
			photoInfo.fileUrl = source.readString();
			photoInfo.fileFolder = source.readString();
			photoInfo.fileName = source.readString();
			photoInfo.fileNamePinYin = source.readString();
			return photoInfo;
		}

		@Override
		public PhotoInfo[] newArray(int size) {
			return new PhotoInfo[size];
		}
	};

}
