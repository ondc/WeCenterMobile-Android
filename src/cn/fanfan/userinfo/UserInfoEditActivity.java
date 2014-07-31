package cn.fanfan.userinfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.callback.Callback;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.fanfan.common.AsyncFileUpLoad;
import cn.fanfan.common.AsyncFileUpLoad.CallBack;
import cn.fanfan.common.AsyncImageGet;
import cn.fanfan.common.CompressAvata;
import cn.fanfan.common.NetworkState;
import cn.fanfan.main.R;
import android.R.string;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoEditActivity extends Activity implements OnClickListener,
		DatePickerDialog.OnDateSetListener {
	private String uid, signature, avatarpath, avatar_file;
	private int sex;// sex (int��1���� 2��Ů 3������)
	private String birthday;// unixʱ���
	private ImageView iv_avatar;
	private EditText et_username, et_introduction;
	private LinearLayout lv_birthday, lv_business, lv_location;
	private TextView tv_sex_f, tv_sex_m, tv_sex_f_background,
			tv_sex_m_background, tv_birthday_info, tv_business_info,
			tv_location_info;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 300;
	private Uri avatarUri;
	SelectPicPopupWindow menuWindow;// ���ͷ�񵯳�ѡ�����ջ���ѡ��ͼ��ĵ����˵�
	protected String errno;
	protected String err;
	protected String job_id;
	protected String user_name;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinformation_edit);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		uid = bundle.getString("uid");
		avatar_file = bundle.getString("avatar_file");
		init();// ��ʼ��
		NetworkState networkState = new NetworkState();
		if (networkState.isNetworkConnected(UserInfoEditActivity.this)) {
			getUserProfile();
		} else {
			Toast.makeText(this, "û�����磬�����Ӻ������", Toast.LENGTH_SHORT).show();
		}
	}

	private void getUserProfile() {
		// TODO Auto-generated method stub
		AsyncHttpClient getUserInfo = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		getUserInfo.get("http://w.hihwei.com/api/profile.php", params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1,
							byte[] responseBody, Throwable arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						Log.i("getUserInfo", responseContent + "---Success");
						JSONTokener jsonParser = new JSONTokener(
								responseContent);
						try {
							JSONObject result = (JSONObject) jsonParser
									.nextValue();
							errno = result.getString("errno");
							err = result.getString("err");
							Log.i("errno", errno);
							Log.i("err", err);
							JSONArray rsm = new JSONArray();
							rsm = result.getJSONArray("rsm");
							Log.i("getJSONArray", rsm.toString());
							// ��������rsm������
							JSONObject rsmcontent = (JSONObject) rsm.get(0);
							Log.i("rsmcontent", rsmcontent.toString());
							JSONTokener jsonParser2 = new JSONTokener(
									rsmcontent.toString());
							JSONObject rsmcontents = (JSONObject) jsonParser2
									.nextValue();
							user_name = rsmcontents.getString("user_name");
							sex = Integer.parseInt(rsmcontents.getString("sex"));//
							birthday = rsmcontents.getString("birthday");
							job_id = rsmcontents.getString("job_id");
							signature = rsmcontents.getString("signature");
							Log.i("user_name", user_name);
							Log.i("birthday", birthday);
							Log.i("signature", signature);
							updateUI();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// �����쳣��
						}
					}
				});
	}

	protected void updateUI() {
		// TODO Auto-generated method stub
		et_username.setText(user_name);
		et_introduction.setText(signature);
		// p
		if (sex == 1) {
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#50FCFF"));
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#DCE0DD"));
		}
		if (sex == 2) {
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#50FCFF"));
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#DCE0DD"));
		}
		// չʾ�û�������Ϣ
		String date = TimeStamp2Date(birthday, "yyyy-MM-dd ");
		tv_birthday_info.setText(date);
		Log.i("date", date);
		if (avatar_file != null) {
			AsyncImageGet getAvatar = new AsyncImageGet(
					"http://w.hihwei.com/uploads/avatar/" + avatar_file,
					iv_avatar);
			getAvatar.execute();
		}
	}

	// ��unixʱ���Java��Unixʱ���ת����ָ����ʽ����
	public String TimeStamp2Date(String timestampString, String formats) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat(formats)
				.format(new java.util.Date(timestamp));
		return date;
	}

	// ��ʼ��
	private void init() {
		// TODO Auto-generated method stub
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		et_username = (EditText) findViewById(R.id.et_uername);
		et_introduction = (EditText) findViewById(R.id.et_introduction);
		lv_birthday = (LinearLayout) findViewById(R.id.lv_birthday);
		lv_business = (LinearLayout) findViewById(R.id.lv_business);
		lv_location = (LinearLayout) findViewById(R.id.lv_location);
		tv_sex_f = (TextView) findViewById(R.id.tv_sex_f);
		tv_sex_m = (TextView) findViewById(R.id.tv_sex_m);
		tv_sex_f_background = (TextView) findViewById(R.id.tv_sex_f_background);
		tv_sex_m_background = (TextView) findViewById(R.id.tv_sex_m_background);
		tv_birthday_info = (TextView) findViewById(R.id.tv_birthday_info);
		tv_business_info = (TextView) findViewById(R.id.tv_business_info);
		tv_location_info = (TextView) findViewById(R.id.tv_location_info);
		iv_avatar.setOnClickListener(this);
		et_username.setOnClickListener(this);
		et_introduction.setOnClickListener(this);
		lv_birthday.setOnClickListener(this);
		lv_business.setOnClickListener(this);
		lv_location.setOnClickListener(this);
		tv_sex_f_background.setOnClickListener(this);
		tv_sex_m_background.setOnClickListener(this);
		tv_sex_m.setOnClickListener(this);
		tv_sex_f.setOnClickListener(this);
		tv_birthday_info.setOnClickListener(this);
		tv_business_info.setOnClickListener(this);
		tv_location_info.setOnClickListener(this);
	}

	/* �������view�ļ��� ������ */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_avatar:
			menuWindow = new SelectPicPopupWindow(UserInfoEditActivity.this,
					itemsOnClick);
			// ��ʾ����
			menuWindow.showAtLocation(UserInfoEditActivity.this
					.findViewById(R.id.infoedit_layout), Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
			break;
		case R.id.et_uername:
			user_name = et_username.getText().toString();
			Log.i("user_name", user_name);
			break;
		case R.id.et_introduction:
			signature = et_introduction.getText().toString();
			Log.i("signature", signature);
			break;
		case R.id.tv_sex_m:
			sex = 1;
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#50FCFF"));
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#DCE0DD"));
			break;
		case R.id.tv_sex_f:
			sex = 2;
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#50FCFF"));
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#DCE0DD"));
			break;
		case R.id.lv_birthday:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
			break;
		case R.id.lv_business:
			Toast.makeText(this, "�ֻ����ݲ�֧�ָ��ģ����¼��վ���ģ�", Toast.LENGTH_LONG)
					.show();
			break;
		case R.id.lv_location:
			Toast.makeText(this, "�ֻ����ݲ�֧�ָ��ģ����¼��վ���ģ�", Toast.LENGTH_LONG)
					.show();
			break;

		default:
			break;
		}
	}

	/* popUpWindows������ѡ�����ջ��ߴ�ͼ��ѡ����Ƭ�Ĳ˵�������item �ļ��������� */
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_pick_photo:// �����
				menuWindow.dismiss();
				Intent openAlbumIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(openAlbumIntent,
						PICK_IMAGE_ACTIVITY_REQUEST_CODE);// �����
				break;
			case R.id.btn_take_photo:// �����
				menuWindow.dismiss();
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				String path = Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED) ? Environment
						.getExternalStorageDirectory().getPath() + "/fanfan"
						: null + "/fanfan";
				File foldFile = new File(path);
				if (!foldFile.exists()) {
					foldFile.mkdir();
				}
				avatarpath = path + File.separator + uid + "avatarImage.jpg";
				File avatarFile = new File(path + File.separator + uid
						+ "avatarImage.jpg");
				avatarUri = Uri.fromFile(avatarFile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);// �����
				break;
			case R.id.btn_cancel:
				menuWindow.dismiss();
				break;
			default:
				break;
			}

		}

	};

	/* ����ϵͳͼ�����������Ĵ��� */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				// ����ͷ����Ƭ�ɹ����ϴ�ͷ��
				upLoadAnim();
				CompressAvata compressAvata = new CompressAvata(avatarpath);// ѹ��������
				AsyncFileUpLoad asyncFileUpLoad = new AsyncFileUpLoad(
						UserInfoEditActivity.this,
						"http://w.hihwei.com/?/api/account/avatar_upload/",
						compressAvata.getCompressAvatarPath(), new CallBack() {
							/* �ϴ���Խ������������ɹ�����ͷ��ʧ�����err��Ϣ */
							@Override
							public void callBack(String preview, String err,
									String errno) {
								Log.i("callbackinfo", preview);
								Log.i("err", err);
								Log.i("errno", errno);
								if (errno == "x") {
									Toast.makeText(UserInfoEditActivity.this,
											"�����е㲻��Ŷ������һ�ΰɣ�", Toast.LENGTH_LONG)
											.show();
								} else {
									AsyncImageGet getAvatarPreview = new AsyncImageGet(
											preview, iv_avatar);
									getAvatarPreview.execute();
								}

							}
						});
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the Pick avatar
			} else {
				// Pick avatar failed, advise user
			}
			break;
		case PICK_IMAGE_ACTIVITY_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				// �����ѡ��ͷ����Ƭ�����
				if (data != null) {
					avatarUri = data.getData();
					Log.i("avatarUri", avatarUri.getPath());
					/* ����uir��media���ݿ��ѯ����ʵ���ļ�·�� */

					String[] proj = { MediaStore.Images.Media.DATA };
					CursorLoader loader = new CursorLoader(
							UserInfoEditActivity.this, avatarUri, proj, null,
							null, null);
					Cursor cursor = loader.loadInBackground();
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					avatarpath = cursor.getString(column_index);

					// ��ȡ��·��
					CompressAvata compressAvata = new CompressAvata(avatarpath);// ѹ��������
					// if (compressAvata.isConformSize()) {
					// Log.i("���ϴ�С", "ok");
					// } else {
					// Log.i("no���ϴ�С", "ok");
					// }
					// �ϴ�ͷ��
					upLoadAnim();// �ϴ�ʱ�Ķ���
					AsyncFileUpLoad asyncFileUpLoad = new AsyncFileUpLoad(
							UserInfoEditActivity.this,
							"http://w.hihwei.com/?/api/account/avatar_upload/",
							compressAvata.getCompressAvatarPath(),
							new CallBack() {
								/* �ϴ���Խ������������ɹ�����ͷ��ʧ�����err��Ϣ */
								@Override
								public void callBack(String preview,
										String err, String errno) {
									// TODO Auto-generated method stub
									Log.i("callbackinfo", preview);
									Log.i("err", err);
									Log.i("errno", errno);
									if (errno == "x") {
										Toast.makeText(
												UserInfoEditActivity.this,
												"�����е㲻��Ŷ������һ�ΰɣ�",
												Toast.LENGTH_LONG).show();
									} else {
										AsyncImageGet getAvatarPreview = new AsyncImageGet(
												preview, iv_avatar);
										getAvatarPreview.execute();
									}
								}
							});
				}

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void upLoadAnim() {
		iv_avatar.setImageResource(R.drawable.ic_loading);
	}

	/* datepicker�Ļص� �����û��趨���պ�Ĳ��� */
	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		Log.i("year", Integer.toString(year));
		Log.i("month", Integer.toString((month + 1)));
		Log.i("day", Integer.toString(day));
		tv_birthday_info.setText(Integer.toString(year) + "-"
				+ Integer.toString((month + 1)) + "-" + Integer.toString(day));
		String dateString = Integer.toString(year) + "-"
				+ Integer.toString((month + 1)) + "-" + Integer.toString(day)
				+ " " + "12:00:00";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date date = sdf.parse(dateString);
			birthday = Long.toString(date.getTime() / 1000);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ʱ��ת���� unixʱ��� ����");
		}

		Log.i("time", birthday);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_complete, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.edit_complete) {
			Log.i("���", "���");
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(et_username.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			upDateProfile();
		}
		return super.onOptionsItemSelected(item);
	}

	private void upDateProfile() {
		// TODO Auto-generated method stub
		signature = et_introduction.getText().toString();
		user_name = et_username.getText().toString();
		AsyncHttpClient upLoadProfile = new AsyncHttpClient();
		RequestParams UpParams = new RequestParams();
		UpParams.put("uid", uid);
		UpParams.put("user_name", user_name);
		UpParams.put("sex", sex);
		UpParams.put("signature", signature);
		UpParams.put("job_id", job_id);
		UpParams.put("birthday", birthday);
		upLoadProfile.post("http://w.hihwei.com/api/profile_setting.php",
				UpParams, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						Log.i("upDateProfile", responseContent + "---Success");
						JSONTokener jsonParser = new JSONTokener(
								responseContent);
						try {
							JSONObject result = (JSONObject) jsonParser
									.nextValue();
							errno = result.getString("errno");
							err = result.getString("err");
							Log.i("errno", errno);
							Log.i("err", err);
							adviseUesr(err);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// �����쳣��
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						Log.i("�ϴ�����ʧ�ܣ�", "x-x");
					}
				});
	}

	private void adviseUesr(String err) {
		if (err != "null") {
			Toast.makeText(this, err, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "�޸ĳɹ���", Toast.LENGTH_LONG).show();
		}
	}
}