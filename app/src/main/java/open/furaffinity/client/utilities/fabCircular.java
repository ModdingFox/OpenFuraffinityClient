package open.furaffinity.client.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            float xCosine = (radius * this.radiusMultiplier) * (float) java.lang.Math.cos(this.angle * Math.PI / 180);
            float ySine = (radius * this.radiusMultiplier) * (float) java.lang.Math.sin(this.angle * Math.PI / 180);

            this.floatingActionButton.setTranslationX(xCosine);
            this.floatingActionButton.setTranslationY(ySine);
        }

        public void setVisibility(boolean visible) {
            if (visible) {
                this.floatingActionButton.show();
            } else {
                this.floatingActionButton.hide();
            }
        }
    }

    private List<FloatingActionButtonContainer> floatingActionButtons = new ArrayList<>();
    private boolean isVisible = false;

    @Override
    public void onClick(View v) {
        for (FloatingActionButtonContainer currentFloatingActionButton : floatingActionButtons) {
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
        floatingActionButton.setVisibility(((isVisible) ? (VISIBLE) : (GONE)));
        FloatingActionButtonContainer newFloatingActionButtonContainer = new FloatingActionButtonContainer(floatingActionButton, radiusMultiplier, angle);
        newFloatingActionButtonContainer.setPosition(this.getWidth());
        floatingActionButtons.add(newFloatingActionButtonContainer);
    }

    public void removeButton(FloatingActionButton floatingActionButton) {
        for (FloatingActionButtonContainer currentFloatingActionButton : floatingActionButtons) {
            currentFloatingActionButton.setPosition(this.getWidth());
            currentFloatingActionButton.setVisibility(false);
        }

        isVisible = false;

        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < floatingActionButtons.size(); i++) {
            if (floatingActionButtons.get(i).getFloatingActionButton().equals(floatingActionButton)) {
                positions.add(i);
            }
        }

        Collections.reverse(positions);

        for (int i : positions) {
            floatingActionButtons.get(i).setVisibility(false);
            floatingActionButtons.remove(i);
        }
    }
}
