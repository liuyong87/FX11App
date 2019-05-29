package com.semisky.multimedia.aidl.video;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 视频实体类
 * 
 * @author liuyong
 * 
 */
public class VideoInfo implements Parcelable {
	private int _id;// 自增长id
	private int usbFlag;// U盘标识
	private int fileType;// 文件件类型
	private String fileUrl;// 文件路径
	private String fileFolder;// 文件所属文件夹
	private String fileName;// 文件名
	private String fileNamePinYin;// 文件名拼音
	private int duration;// 总时长

	public VideoInfo() {
		super();
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "VideoInfo [_id=" + _id + ", usbFlag=" + usbFlag + ", fileType=" + fileType + ", fileUrl=" + fileUrl
				+ ", fileFolder=" + fileFolder + ", fileName=" + fileName + ", fileNamePinYin=" + fileNamePinYin
				+ ", duration=" + duration + "]";
	}

	public static Creator<VideoInfo> getCreator() {
		return CREATOR;
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
		dest.writeInt(this.duration);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {

		@Override
		public VideoInfo createFromParcel(Parcel source) {
			VideoInfo videoInfo = new VideoInfo();
			videoInfo._id = source.readInt();
			videoInfo.usbFlag = source.readInt();
			videoInfo.fileType = source.readInt();
			videoInfo.fileUrl = source.readString();
			videoInfo.fileName = source.readString();
			videoInfo.fileNamePinYin = source.readString();
			videoInfo.duration = source.readInt();
			return videoInfo;
		}

		@Override
		public VideoInfo[] newArray(int size) {
			return new VideoInfo[size];
		}
	};

}
