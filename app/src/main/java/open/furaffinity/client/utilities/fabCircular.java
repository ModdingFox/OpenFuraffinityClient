package open.furaffinity.client.utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import open.furaffinity.client.R;

public class fabCircular extends FloatingActionButton implements View.OnClickListener {

    private class FloatingActionButtonContainer {
        private FloatingActionButton floatingActionButton;
        private float radiusMultiplier;
        private float angle;

        public FloatingActionButtonContainer(FloatingActionButton floatingActionButton, float radiusMultiplier, float angle) {
            this.floatingActionButton = floatingActionButton;
            this.radiusMultiplier = radiusMultiplier;
            this.angle = angle;
        }

        public FloatingActionButton getFloatingActionButton() {
            return floatingActionButton;
        }

        public void setradiusMultiplier(float radiusMultiplier) {
            this.radiusMultiplier = radiusMultiplier;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public void setPosition(float radius) {
            float xCosine = (radius * this.radiusMultiplier) * (float)java.lang.Math.cos(this.angle * Math.PI / 180);
            float ySine = (radius * this.radiusMultiplier) * (float)java.lang.Math.sin(this.angle * Math.PI / 180);

            this.floatingActionButton.setTranslationX(xCosine);
            this.floatingActionButton.setTranslationY(ySine);
        }

        public void setVisibility(boolean visible) {
            if(visible) {
                this.floatingActionButton.setVisibility(VISIBLE);
            } else {
                this.floatingActionButton.setVisibility(GONE);
            }
        }
    }

    private List<FloatingActionButtonContainer> floatingActionButtons = new ArrayList<>();
    private boolean isVisible = false;

    @Override
    public void onClick(View v) {
        for(FloatingActionButtonContainer currentFloatingActionButton : floatingActionButtons) {
            currentFloatingActionButton.setPosition(this.getWidth());
            currentFloatingActionButton.setVisibility(!isVisible);
        }

        isVisible = !isVisible;
    }

    public fabCircular(@NonNull Context context) {
        super(context);
        setOnClickListener(this::onClick);
    }

    public fabCircular(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this::onClick);
    }

    public fabCircular(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this::onClick);
    }

    public void addButton(FloatingActionButton floatingActionButton, float radiusMultiplier, float angle) {
        floatingActionButton.setLayoutParams(this.getLayoutParams());
        floatingActionButton.setTranslationX(this.getTranslationX());
        floatingActionButton.setTranslationY(this.getTranslationY());
        floatingActionButton.setVisibility(GONE);
        floatingActionButtons.add(new FloatingActionButtonContainer(floatingActionButton, radiusMultiplier, angle));
    }

    public boolean removeButton(FloatingActionButton floatingActionButton) {
        int id = -1;
        for(int i = 0; i < floatingActionButtons.size(); i++) {
            if(floatingActionButtons.get(i).getFloatingActionButton().equals(floatingActionButton)) {
                id = i;
            }
        }

        if(id != -1) {
            floatingActionButtons.get(id).setVisibility(false);
            floatingActionButtons.remove(id);
            return true;
        }

        return false;
    }
}