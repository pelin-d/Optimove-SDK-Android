package com.optimove.android.optimovemobilesdk.ui.gaming;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.optimove.android.optimovemobilesdk.R;
import com.optimove.android.optimovemobilesdk.constants.Constants;
import com.optimove.android.optimovemobilesdk.databinding.FragmentGamingBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

import java.util.Random;

public class GamingFragment extends BaseFragment {

    private FragmentGamingBinding binding;
    private static final Random random = new Random();
    ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGamingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView = binding.diceImage;
        MaterialButton rollDiceButton = binding.rollDiceButton;

        rollDiceButton.setOnClickListener(this::rollDice);

        setScreenInfo("Gaming");

        return root;
    }

    public void rollDice(View v) {
        int side = random.nextInt(6) + 1;
        int imageId = 0;
        switch (side) {
            case 1:
                imageId = R.drawable.dice_1;
                break;
            case 2:
                imageId = R.drawable.dice_2;
                break;
            case 3:
                imageId = R.drawable.dice_3;
                break;
            case 4:
                imageId = R.drawable.dice_4;
                break;
            case 5:
                imageId = R.drawable.dice_5;
                break;
            case 6:
                imageId = R.drawable.dice_6;
                break;
        }
        Log.d(Constants.TAG, "Rolled " + side);

        Animation rotateAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.rotate_360_anim);
        imageView.startAnimation(rotateAnimation);

        Drawable d = ResourcesCompat.getDrawable(getResources(), imageId, null);
        imageView.setImageDrawable(d);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
