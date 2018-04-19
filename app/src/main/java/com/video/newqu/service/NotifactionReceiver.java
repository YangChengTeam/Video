package com.video.newqu.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.NotifactionActionInfo;
import com.video.newqu.bean.NotifactionMessageInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.ui.activity.MainActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.activity.WebViewActivity;
import com.video.newqu.util.Logger;
import com.video.newqu.util.NotifactionUtil;
import com.video.newqu.util.Utils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PushReceiver;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 极光推送自定义接收器
 */

public class NotifactionReceiver extends PushReceiver {

	private  final String TAG = NotifactionReceiver.class.getSimpleName();
	/**
	 * @param context
	 * @param intent
     */
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			printBundle(bundle);
			//注册成功
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

			//接收到自定义消息
			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

			//接收到通知
			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
				if(!TextUtils.isEmpty(extras)){
					try {
						NotifactionMessageInfo messageInfo = new Gson().fromJson(extras, NotifactionMessageInfo.class);
						//只将通知类型的消息保存至本地
						if(null!=messageInfo&&null!=messageInfo.getAdd_time()&&messageInfo.getAdd_time().length()>0&&0==messageInfo.getMsg_type()){
							messageInfo.setId((long) notifactionId);
							List<NotifactionMessageInfo> messageList= (List<NotifactionMessageInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(VideoApplication.getLoginUserID()+Constant.CACHE_USER_MESSAGE);
							int badgeCount=0;
							if(null==messageList) messageList=new ArrayList<>();
							messageList.add(messageInfo);
							ApplicationManager.getInstance().getCacheExample().put(VideoApplication.getLoginUserID()+Constant.CACHE_USER_MESSAGE, (Serializable) messageList);//刷新本地缓存
							if(null!=messageList&&messageList.size()>0){
								for (NotifactionMessageInfo notifactionMessageInfo : messageList) {
									if(!notifactionMessageInfo.isRead()){
										badgeCount++;
									}
								}
								//处理桌面图标
								if(badgeCount>0){
									ShortcutBadger.applyCount(context.getApplicationContext(), badgeCount); //for 1.1.4+
								}
							}
							//发送消息给主界面，有新的消息收到了
							MessageEvent messageEvent = new MessageEvent();
							messageEvent.setMessage(Constant.EVENT_NEW_MESSAGE);
							messageEvent.setExtar(badgeCount);
							EventBus.getDefault().post(messageEvent);
						}
					}catch (Exception e){
					}
                }
			//用户点击通知
			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
				if(!NotifactionUtil.isEmpty(extras)){
					JSONObject extraJson = new JSONObject(extras);
					if(null!=extraJson&&extraJson.length()>0){
						String msg_type = extraJson.getString("msg_type");
						Intent startIntent=new Intent();
						if(!TextUtils.isEmpty(msg_type)){
							//视频类型的消息
							if(TextUtils.equals("0",msg_type)){
								String video_id = extraJson.getString("video_id");
								startIntent.setClass(context,VideoDetailsActivity.class);
								startIntent.putExtra("video_id",video_id);
								startIntent.putExtra("video_author_id", VideoApplication.getLoginUserID());
								//网页
							}else if(TextUtils.equals("1",msg_type)){
								String url = extraJson.getString("url");
								startIntent.setClass(context,WebViewActivity.class);
								startIntent.putExtra("url",url);
								startIntent.putExtra("title", extraJson.getString("title"));
							//内部动作,支持最大携带5个参数
							}else if(TextUtils.equals("2",msg_type)){
								NotifactionActionInfo messageInfo = new Gson().fromJson(extras, NotifactionActionInfo.class);
								if(null!=messageInfo&&!TextUtils.isEmpty(messageInfo.getAction())){
									Class clazz = Class.forName("com.video.newqu.ui.activity." +messageInfo.getAction());
									startIntent.setClass(context,clazz);
									if(!TextUtils.isEmpty(messageInfo.getKey1())&&messageInfo.getKey1().length()>0&&!TextUtils.isEmpty(messageInfo.getValue1())){
										if(messageInfo.getValue1().startsWith("0x")){
											startIntent.putExtra(messageInfo.getKey1(),Integer.parseInt(messageInfo.getValue1()));
										}else{
											startIntent.putExtra(messageInfo.getKey1(),messageInfo.getValue1());
										}
									}
									if(!TextUtils.isEmpty(messageInfo.getKey2())&&messageInfo.getKey2().length()>0&&!TextUtils.isEmpty(messageInfo.getValue2())){
										if(messageInfo.getValue2().startsWith("0x")){
											startIntent.putExtra(messageInfo.getKey2(),Integer.parseInt(messageInfo.getValue2()));
										}else{
											startIntent.putExtra(messageInfo.getKey2(),messageInfo.getValue2());
										}
									}
									if(!TextUtils.isEmpty(messageInfo.getKey3())&&messageInfo.getKey3().length()>0&&!TextUtils.isEmpty(messageInfo.getValue3())){
										if(messageInfo.getValue3().startsWith("0x")){
											startIntent.putExtra(messageInfo.getKey3(),Integer.parseInt(messageInfo.getValue3()));
										}else{
											startIntent.putExtra(messageInfo.getKey3(),messageInfo.getValue3());
										}
									}
									if(!TextUtils.isEmpty(messageInfo.getKey4())&&messageInfo.getKey4().length()>0&&!TextUtils.isEmpty(messageInfo.getValue4())){
										if(messageInfo.getValue4().startsWith("0x")){
											startIntent.putExtra(messageInfo.getKey4(),Integer.parseInt(messageInfo.getValue4()));
										}else{
											startIntent.putExtra(messageInfo.getKey4(),messageInfo.getValue4());
										}
									}
									if(!TextUtils.isEmpty(messageInfo.getKey5())&&messageInfo.getKey5().length()>0&&!TextUtils.isEmpty(messageInfo.getValue5())){
										if(messageInfo.getValue5().startsWith("0x")){
											startIntent.putExtra(messageInfo.getKey5(),Integer.parseInt(messageInfo.getValue5()));
										}else{
											startIntent.putExtra(messageInfo.getKey5(),messageInfo.getValue5());
										}
									}
									if(!TextUtils.isEmpty(messageInfo.getKey6())&&messageInfo.getKey6().length()>0&&!TextUtils.isEmpty(messageInfo.getValue6())){
										if(messageInfo.getValue6().startsWith("0x")){
											startIntent.putExtra(messageInfo.getKey6(),Integer.parseInt(messageInfo.getValue6()));
										}else{
											startIntent.putExtra(messageInfo.getKey6(),messageInfo.getValue6());
										}
									}
								}else{
									//意图为空，直接打开主页
									startIntent.setClass(context, MainActivity.class);
									return;
								}
							}else{
								//无指定类型-直接主页
								startMainActivity(context);
								return;
							}
							try {
								startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(startIntent);
								return;
							}catch (Exception e){
								//跳转界面失败，去主页
								startMainActivity(context);
								return;
							}
						}else{
							//无指定动作-直接主页
							startIntent.setClass(context, MainActivity.class);
							return;
						}
					}else{
						//参数转换失败，直接主页
						startMainActivity(context);
						return;
					}
				}else{
					//参数为空，直接主页
					startMainActivity(context);
					return;
				}
			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {

			} else {

			}
		} catch (Exception e){

		}
	}

	private void startMainActivity(Context context) {
		//程序未启动
		if(3==Utils.getAppSatus(context,"com.video.newqu")){
			//启动开屏页
			String packageName = context.getApplicationContext().getPackageName();
			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(launchIntent);
			//程序已经在前台或后台运行
		}else{

			Intent startIntent=new Intent();
			startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startIntent.setClass(context, MainActivity.class);
			context.startActivity(startIntent);
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {

					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {

				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
}
