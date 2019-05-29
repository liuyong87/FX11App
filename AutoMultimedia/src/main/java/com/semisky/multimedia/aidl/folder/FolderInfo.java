package com.semisky.multimedia.aidl.folder;

import android.os.Parcel;
import android.os.Parcelable;

import com.semisky.multimedia.common.constants.Definition;


public class FolderInfo implements Parcelable {

	private int usbFlag;// U盘标识
	/**
	 * {@link Definition.MediaFileType#TYPE_MUSIC}
	 * <p>
	 * {@link Definition.MediaFileType#TYPE_VIDEO}
	 * <p>
	 * {@link Definition.MediaFileType#TYPE_PHOTO}
	 * <p>
	 * {@link Definition.MediaFileType#TYPE_FOLDER}
	 */
	private int type;// 文件类型
	private String url;// 文件或文件夹URL
	private String name;// 文件或文件夹名字
	private String namePinYin;// 文件或文件夹名字

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.usbFlag);
		dest.writeInt(this.type);
		dest.writeString(this.url);
		dest.writeString(this.name);
		dest.writeString(this.namePinYin);
	}

	public static final Creator<FolderInfo> CREATOR = new Creator<FolderInfo>() {

		@Override
		public FolderInfo createFromParcel(Parcel source) {
			FolderInfo info = new FolderInfo();
			info.usbFlag = source.readInt();
			info.type = source.readInt();
			info.url = source.readString();
			info.name = source.readString();
			info.namePinYin = source.readString();
			return info;
		}

		@Override
		public FolderInfo[] newArray(int size) {
			return new FolderInfo[size];
		}
	};

	public int getUsbFlag() {
		return usbFlag;
	}

	public void setUsbFlag(int usbFlag) {
		this.usbFlag = usbFlag;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamePinYin() {
		return namePinYin;
	}

	public void setNamePinYin(String namePinYin) {
		this.namePinYin = namePinYin;
	}

	public static Creator<FolderInfo> getCreator() {
		return CREATOR;
	}

	@Override
	public String toString() {
		return "FolderInfo [usbFlag=" + usbFlag + ", type=" + type + ", url=" + url + ", name=" + name
				+ ", namePinYin=" + namePinYin + "]";
	}

	@Override
	public boolean equals(Object o) {
		FolderInfo obj = (FolderInfo) o;
		return (obj.name.equals(this.name));
	}

}
