package kankan.wheel.widget.adapters;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 老虎机适配器
 */
public class SlotMachineAdapter extends AbstractWheelAdapter {
	// 图片的大小
	final int IMAGE_WIDTH = 120;
	final int IMAGE_HEIGHT = 120;

	// 图片的数组
	private final int items[] = new int[] { 
			android.R.drawable.star_big_on,
			android.R.drawable.stat_sys_warning,
			android.R.drawable.radiobutton_on_background,
			android.R.drawable.ic_delete };

	// 对图片的缓存
	private List<SoftReference<Bitmap>> images;

	// 布局膨胀器
	private Context context;

	/**
	 * 构造函数
	 */
	public SlotMachineAdapter(Context context) {
		this.context = context;
		images = new ArrayList<SoftReference<Bitmap>>(items.length);
		for (int id : items) {
			images.add(new SoftReference<Bitmap>(loadImage(id)));
		}
	}

	/**
	 * 从资源加载图片
	 */
	private Bitmap loadImage(int id) {
		Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), id);
		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH,
				IMAGE_HEIGHT, true);
		bitmap.recycle();
		return scaled;
	}

	@Override
	public int getItemsCount() {
		return items.length;
	}

	// 设置图片布局的参数
	final LayoutParams params = new LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		ImageView img;
		if (cachedView != null) {
			img = (ImageView) cachedView;
		} else {
			img = new ImageView(context);
		}
		img.setLayoutParams(params);
		SoftReference<Bitmap> bitmapRef = images.get(index);
		Bitmap bitmap = bitmapRef.get();
		if (bitmap == null) {
			bitmap = loadImage(items[index]);
			images.set(index, new SoftReference<Bitmap>(bitmap));
		}
		img.setImageBitmap(bitmap);

		return img;
	}
}