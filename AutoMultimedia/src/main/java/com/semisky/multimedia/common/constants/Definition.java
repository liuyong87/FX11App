package com.semisky.multimedia.common.constants;

public class Definition {
	public static final boolean DEBUG = true;
	public static boolean DEBUG_ENG = false;// 工程模式调试标识

	public static final String KEY_USB_FLAG = "usbFlag";// 意图传递参数
	public static final int FLAG_USB1 = 1;// USB1标识
	public static final int FLAG_USB2 = 2;// USB2标识
	public static final int FLAG_USB_INVALID = -1;// 无效USB2标识
	public static final String CURRENT_PLATFORM_KEYWORDS = "BM2718";// 当前平台关键字
	public static final String FACTORY_TEST_ACTION_MUSIC = "factoryTestMusic";//出厂音乐测试
	public static final String FACTORY_TEST_ACTION_VIDEO = "factoryTestVideo";//出厂视频测试
	public static final String FACTORY_TEST_ACTION_PICTURE = "factoryTestPicture";//出厂图片测试

	// 当前平台USB路径
	public static final String PATH_USB1 = "/storage/udisk0";// 当前平台USB1绝对路径
	public static final String PATH_USB2 = "/storage/udisk1";// 当前平台USB2绝对路径
	// 两个平台USB路径
	public static final String PATH_USB1_BM2718_PLATFORM = "/storage/udisk0";// BM2718平台USB1绝对路径
	public static final String PATH_USB2_BM2718_PLATFORM = "/storage/udisk1";// BM2718平台USB2绝对路径
	public static final String PATH_USB1_OTHER_PLATFORM = "/storage/udisk";// 其它平台USB1绝对路径
	public static final String PATH_USB2_OTHER_PLATFORM = "/storage/udisk2";// 其它平台USB2绝对路径
	// USB1 扩展目录
	public static final String PATH_USB1_DIR_EXTEND = "/udisk00";

	// USB广播意图
	public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
	public static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";

	// 多媒体列表包名和类名
	public static final String MEDIA_LIST_PKG = "com.semisky.multimedia";
	public static final String Media_LSIT_CLZ = "com.semisky.multimedia.media_list.MultimediaListActivity";

	public class AppFlag{
		public static final int TYPE_MUSIC = 1;
		public static final int TYPE_MUSIC_LIST = -9;
		public static final int TYPE_VIDEO = 2;
		public static final int TYPE_VIDEO_VIDEO = -8;
		public static final int TYPE_PHOTO = 3;
		public static final int TYPE_PHOTO_LIST = -7;
		public static final int TYPE_LIST = 4;
		public static final int TYPE_INVALID = -1;

	}

	// 媒体列表常量
	public class MediaListConst{
		public static final String FRAGMENT_FLAG = "fragmentFlag";
		public static final int FRAGMENT_LIST_USB1_MUSIC = 0;
		public static final int FRAGMENT_LIST_USB2_MUSIC = 1;
		public static final int FRAGMENT_LIST_BT_MUSIC = 2;

		public static final int FRAGMENT_LIST_USB1_BY_VIDEO = 0;
		public static final int FRAGMENT_LIST_USB2_BY_VIDEO = 1;

		public static final int FRAGMENT_LIST_USB1_BY_PHOTO = 0;
		public static final int FRAGMENT_LIST_USB2_BY_PHOTO = 1;
	}

	public class MediaStorageConst {
		public static final String ACTION_FACTORY_TEST = "com.semisky.service.ACTION_FACTORY_TEST";
		public static final String ACTION_DEBUG = "com.semisky.service.ACTION_DEBUG";
		public static final int CMD_PRINT_LOG = -100;// 打印日志命令标识
		// 媒体存储服务意图名字
		public static final String ACTION_OPS_CONTROL = "com.semisky.service.ACTION_OPS_CONTROL";
		public static final String PARAM_CMD = "cmd";// 命令参数
		public static final int CMD_USB_MOUNTED = 10;// 开始U盘扫描命令标识
		public static final int CMD_USB_UNMOUNTED = 11;// 停止U盘扫描命令标识
		public static final int CMD_DEL_DB_DATA = 12;// 删除数据库媒体数据
		public static final String PARAM_USB_PATH = "usbPath";// 携带U盘路径参数
	}

	// 媒体文件类型
	public class MediaFileType {
		public static final int TYPE_MUSIC = 0;
		public static final int TYPE_VIDEO = 1;
		public static final int TYPE_PHOTO = 2;
		public static final int TYPE_LRC = 3;// 歌词文件类型
		public static final int TYPE_FOLDER = -1;
		public static final int TYPE_BACK_DIR = -2;
	}

	// 媒体播放控制常量
	public class MediaCtrlConst {
		public static final String SERVICE_PKG = "com.semisky.multimedia";
		public static final String SERVICE_CLZ = "com.semisky.multimedia.media_music.service.LocalMusicService";
		// 音乐服务意图名字
		public static final String ACTION_SERVICE_MUSIC_PLAY_CONTROL = "com.semisky.service.ACTION_MUSIC_PLAY_CONTROL";
		public static final String ACTION_SERVICE_MUSIC_DEBUG_LOG = "com.semisky.service.ACTION_MUSIC_DEBUG_LOG";
		// 音乐广播意图名字
		public static final String ACTION_BROADCAST_MUSIC_PLAY_CONTROL = "com.semisky.broadcast.ACTION_MUSIC_PLAY_CONTROL";
		// 视频广播意图名字
		public static final String ACTION_BROADCAST_VIDEO_PLAY_CONTROL = "com.semisky.broadcast.ACTION_VIDEO_PLAY_CONTROL";
		// 播放控件命令参数 {注:负数代表:控制命令(-1 ~ ...) , 正数代表:播放曲目下标(1 ~ ....)}
		public static final String PARAM_CMD = "cmd";// 命令参数
		public static final String PARAM_URL = "url";// 命令参数
		public static final int CMD_INVALID = -65535;// 播放节目命令标识
		public static final int CMD_RESUME_PLAY = -100;// 恢复媒体资源播放命令标识
		public static final int CMD_PREV = -101;// 上一个节目命令标识
		public static final int CMD_NEXT = -102;// 下一个节目命令标识
		public static final int CMD_PAUSE = -103;// 暂停节目命令标识
		public static final int CMD_START = -104;// 节目播放命令标识(预置条件：之前是暂停播放)
		public static final int CMD_LIST_PLAY = -106;// 列表媒体播放命令标识
		public static final int CMD_STOP = -107;// 停止播放命令标识
		public static final int CMD_PLAY_INDEX = -108;
		public static final int CMD_PLAY_TOGGLE = -105;// 播放切换命令标识 单曲循环
		public static final int CMD_PLAY_PLAY_LOOP = -110;// 列表循环播放模式
		public static final int CMD_PLAY_PLAY_SHUFFLE = -111;// 列表循环播放模式
		public static final int CMD_AUDIO_SOURCE_USB1 = -112;// 音源USB1
		public static final int CMD_AUDIO_SOURCE_USB2 = -113;// 音源USB2

	}
	
	// 多媒体音乐播放服务状态常量
	public class MusicServiceConst{
		public static final int PLAYER_STATE_INVALIED = -1;
		public static final int PLAYER_STATE_LOSS_AUDIOFOCUS = -2;
		public static final int PLAYER_STATE_PLAY = 1;
		public static final int PLAYER_STATE_PAUSE =2;
	}

}
