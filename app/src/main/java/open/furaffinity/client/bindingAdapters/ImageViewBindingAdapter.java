package open.furaffinity.client.bindingAdapters;

import android.content.Context;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;

public final class ImageViewBindingAdapter {

    private ImageViewBindingAdapter() {

    }

    @BindingAdapter("image")
    public static void loadImage(ImageView imageView, String imageUrl) {
        final Context context = imageView.getContext();
        if (imageUrl == null || imageUrl.isEmpty()) {
            Glide.with(context).clear(imageView);
        }
        else {
            Glide.with(context).load(imageUrl).into(imageView);
        }
    }
}
