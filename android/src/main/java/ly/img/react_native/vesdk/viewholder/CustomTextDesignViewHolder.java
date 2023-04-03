package ly.img.react_native.vesdk.viewholder;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.model.state.UiConfigTheme;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomTextDesignItem;
import ly.img.react_native.vesdk.view.CustomVideoEditorActivity;

public class CustomTextDesignViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomTextDesignItem, Void> implements View.OnClickListener {
    @NonNull
    public final View contentHolder;
    @Nullable
    protected final ImageSourceView imageView;
    private final AppCompatImageView nixPlusIcon;
    private Paint paint = new Paint();
    private boolean isEnabled;

    @Keep
    public CustomTextDesignViewHolder(@NonNull View v) {
        super(v);
        this.imageView = v.findViewById(R.id.image);
        this.contentHolder = v.findViewById(R.id.contentHolder);
        this.nixPlusIcon = v.findViewById(R.id.nixPlusText);
        this.contentHolder.setOnClickListener(this);
        int theme = this.getStateHandler().getSettingsModel(UiConfigTheme.class).getTheme();
        TypedArray typedArray = this.itemView.getContext().obtainStyledAttributes(theme, new int[]{R.attr.imgly_icon_color});
        int thumbColor = typedArray.getColor(0, -1);
        typedArray.recycle();
        ColorMatrix inkColorMatrix = new ColorMatrix();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0F);
        colorMatrix.postConcat(new ColorMatrix(new float[]{(float) Color.red(thumbColor) / 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float)Color.green(thumbColor) / 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float)Color.blue(thumbColor) / 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float)Color.alpha(thumbColor) / 255.0F, 0.0F}));
        colorMatrix.postConcat(inkColorMatrix);
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    protected void bindData(CustomTextDesignItem data) {
        isEnabled = data.isEnabled();
        nixPlusIcon.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        ImageSource imageSourceIdle = data.getThumbnailSource();
        if (this.imageView != null) {
            this.imageView.setImageSource(imageSourceIdle);
            this.imageView.setLayerType(2, this.paint);
        }

    }

    public void setSelectedState(boolean selected) {
        this.contentHolder.setSelected(selected);
    }

    public void onClick(View v) {
        CustomVideoEditorActivity.closeTooltip();
        this.dispatchSelection();
        this.dispatchOnItemClick();
    }
}
