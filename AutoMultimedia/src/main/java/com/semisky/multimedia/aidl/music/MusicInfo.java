package com.semisky.multimedia.aidl.music;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfo implements Parcelable {
	private int _id;// 自增长id
	private int usbFlag;// U盘标识
	private int fileType;// 文件类型
	private String url;// 路径
	private String fileFolder;// 文件所属文件夹
	private String title;// 歌曲名（包含后缀）
	private String titlePinYing;// 歌曲名全拼
	private String artist;// 演唱者
	private String artistPinYing;// 演唱者全拼
	private String album;// 专辑
	private String albumPinYing;// 专辑全拼
	private int duration;// 总时长
	private int favorite;// 歌曲收藏标识(0:未收藏,1:收藏)

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeInt(this.usbFlag);
		dest.writeInt(this.fileType);
		dest.writeString(this.url);
		dest.writeString(this.fileFolder);
		dest.writeString(this.title);
		dest.writeString(this.titlePinYing);
		dest.writeString(this.artist);
		dest.writeString(this.artistPinYing);
		dest.writeString(this.album);
		dest.writeString(this.albumPinYing);
		dest.writeInt(this.duration);
		dest.writeInt(this.favorite);
	}

	public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {

		@Override
		public MusicInfo createFromParcel(Parcel source) {
			MusicInfo musicInfo = new MusicInfo();
			musicInfo._id = source.readInt();
			musicInfo.usbFlag = source.readInt();
			musicInfo.fileType = source.readInt();
			musicInfo.url = source.readString();
			musicInfo.fileFolder = source.readString();
			musicInfo.title = source.readString();
			musicInfo.titlePinYing = source.readString();
			musicInfo.artist = source.readString();
			musicInfo.artistPinYing = source.readString();
			musicInfo.album = source.readString();
			musicInfo.albumPinYing = source.readString();
			musicInfo.duration = source.readInt();
			musicInfo.favorite = source.readInt();
			return musicInfo;
		}

		@Override
		public MusicInfo[] newArray(int size) {
			return new MusicInfo[size];
		}
	};

	public MusicInfo() {
		super();
	}

	public MusicInfo(String title) {
		this.title = title;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileFolder() {
		return fileFolder;
	}

	public void setFileFolder(String fileFolder) {
		this.fileFolder = fileFolder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlePinYing() {
		return titlePinYing;
	}

	public void setTitlePinYing(String titlePinYing) {
		this.titlePinYing = titlePinYing;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getArtistPinYing() {
		return artistPinYing;
	}

	public void setArtistPinYing(String artistPinYing) {
		this.artistPinYing = artistPinYing;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbumPinYing() {
		return albumPinYing;
	}

	public void setAlbumPinYing(String albumPinYing) {
		this.albumPinYing = albumPinYing;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

	@Override
	public String toString() {
		return "MusicInfo [_id=" + _id + ", usbFlag=" + usbFlag + ", fileType=" + fileType + ", url=" + url
				+ ", fileFolder=" + fileFolder + ", title=" + title + ", titlePinYing=" + titlePinYing + ", artist="
				+ artist + ", artistPinYing=" + artistPinYing + ", album=" + album + ", albumPinYing=" + albumPinYing
				+ ", duration=" + duration + ", favorite=" + favorite + "]";
	}

	

}
