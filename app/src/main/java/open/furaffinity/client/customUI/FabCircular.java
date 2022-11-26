package open.furaffinity.client.customUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public final class FabCircular extends FloatingActionButton implements View.OnClickListener {
    private static final int RADIAN_DEGREE_CONVERSION = 180;

    private final List<FloatingActionButtonContainer> floatingActionButtons = new ArrayList<>();
    private boolean isVisible;

    public FabCircular(@NonNull Context context) {
        super(context);
        setOnClickListener(this);
    }

    public FabCircular(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public FabCircular(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        for (FloatingActionButtonContainer currentFloatingActionButton : floatingActionButtons) {
            currentFloatingActionButton.setPosition(this.getWidth());
            currentFloatingActionButton.setVisibility(!isVisible);
        }

        isVisible = !isVisible;
    }

    public void addButton(
        FloatingActionButton floatingActionButton,
        float radiusMultiplier,
        float angle
    ) {
        floatingActionButton.setLayoutParams(this.getLayoutParams());
        floatingActionButton.setTranslationX(this.getTranslationX());
        floatingActionButton.setTranslationY(this.getTranslationY());
        if (isVisible) {
            floatingActionButton.setVisibility(VISIBLE);
        }
        else {
            floatingActionButton.setVisibility(GONE);
        }
        final FloatingActionButtonContainer newFloatingActionButtonContainer =
            new FloatingActionButtonContainer(floatingActionButton, radiusMultiplier, angle);
        newFloatingActionButtonContainer.setPosition(this.getWidth());
        floatingActionButtons.add(newFloatingActionButtonContainer);
    }

    public void removeButton(FloatingActionButton floatingActionButton) {
        for (FloatingActionButtonContainer currentFloatingActionButton : floatingActionButtons) {
            currentFloatingActionButton.setPosition(this.getWidth());
            currentFloatingActionButton.setVisibility(false);
        }

        isVisible = false;

        final List<Integer> positions = new ArrayList<>();

        for (int position = 0; position < floatingActionButtons.size(); position++) {
            if (floatingActionButtons.get(position).getFloatingActionButton()
                .equals(floatingActionButton)) {
                positions.add(position);
            }
        }

        Collections.reverse(positions);

        for (int position : positions) {
            floatingActionButtons.get(position).setVisibility(false);
            floatingActionButtons.remove(position);
        }
    }

    private static class FloatingActionButtonContainer {
        private final FloatingActionButton floatingActionButton;
        private final float radiusMultiplier;
        private final float angle;

        FloatingActionButtonContainer(
            FloatingActionButton floatingActionButton,
            float radiusMultiplier,
            float angle
        ) {
            this.floatingActionButton = floatingActionButton;
            this.radiusMultiplier = radiusMultiplier;
            this.angle = angle;
        }

        public FloatingActionButton getFloatingActionButton() {
            return floatingActionButton;
        }

        public void setPosition(float radius) {
            final float distance = radius * this.radiusMultiplier;
            final double position = this.angle * Math.PI / RADIAN_DEGREE_CONVERSION;
            final float xCosine = distance * (float) java.lang.Math.cos(position);
            final float ySine = distance * (float) java.lang.Math.sin(position);

            this.floatingActionButton.setTranslationX(xCosine);
            this.floatingActionButton.setTranslationY(ySine);
        }

        public void setVisibility(boolean visible) {
            if (visible) {
                this.floatingActionButton.show();
            }
            else {
                this.floatingActionButton.hide();
            }
        }
    }
}
