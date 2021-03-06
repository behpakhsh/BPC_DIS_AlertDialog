package bpc.dis.alertdialog;

import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bpc.dis.recyclerutilities.SpaceItemDecoration.SpaceItemDecoration;
import bpc.dis.utilities.DialogSizeHelper.DialogSizeHelper;
import bpc.dis.utilities.MeasureHelper.MeasureHelper;
import bpc.dis.utilities.TextSizeHelper.TextSizeHelper;
import network.bpc.bpc_dis_network.NetworkChangeListener;
import network.bpc.bpc_dis_network.NetworkChangeReceiver;

public class AlertDialog extends DialogFragment {

    private RecyclerView rvButtons;
    private ConstraintLayout clAlertDialog;
    private AppCompatImageButton btnClose;
    private AppCompatTextView txtMessage;

    private AlertCloseListener alertCloseListener;
    private AlertClickListener alertClickListener;
    private double width;
    private double height;
    private int backgroundRes;
    private String messageText;
    private float messageTextSize;
    private int messageTextColor;
    private Drawable closeDrawable;
    private int closeDrawableRes;
    private int closeTintColorRes;
    private int maxRowButton;
    private boolean closeEnable;
    private Typeface font;
    private float buttonMargin;
    private List<AlertButton> alertButtons;
    private boolean networkReceiverIsEnable;
    private NetworkReceiverListener networkReceiverListener;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean animationEnable;
    private boolean isFirstRun = true;
    private AlertType alertType;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }
        }

        initValues();

        View view = null;
        switch (alertType) {
            case ALERT:
                view = inflater.inflate(R.layout.dialog_fragment_alert, container, false);
                break;
            case PERMISSION_ALERT:
                view = inflater.inflate(R.layout.dialog_fragment_permision_alert, container, false);
                break;
        }
        if (view != null) {
            btnClose = view.findViewById(R.id.btn_close);
            txtMessage = view.findViewById(R.id.txt_message);
            rvButtons = view.findViewById(R.id.rv_buttons);
            clAlertDialog = view.findViewById(R.id.cl_alert_dialog);

            setDialogAttribute();
            setCloseAttribute();
            setMessageAttribute();
            setButtonAttribute();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        if (animationEnable) {
            if (getDialog() != null) {
                if (getDialog().getWindow() != null) {
                    getDialog().getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerNetworkChangeReceiver();
        DialogSizeHelper.setDialogSize(getDialog(), getContext(), width, height);
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterNetworkChangeReceiver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        alertButtons = null;
        alertCloseListener = null;
        alertClickListener = null;
        networkReceiverListener = null;
        networkChangeReceiver = null;
    }

    private void registerNetworkChangeReceiver() {
        if (networkReceiverIsEnable) {
            if (networkChangeReceiver == null) {
                networkChangeReceiver = new NetworkChangeReceiver(new NetworkChangeListener() {
                    @Override
                    public void onNetworkStateChanged(boolean isOnline) {
                        if (isFirstRun) {
                            isFirstRun = false;
                            return;
                        }
                        networkReceiverListener.onNetworkChange(AlertDialog.this);
                    }
                });
            }
            if (getActivity() != null) {
                getActivity().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }
    }

    private void unRegisterNetworkChangeReceiver() {
        if (networkReceiverIsEnable) {
            if (networkChangeReceiver == null) {
                return;
            }
            if (getActivity() != null) {
                getActivity().unregisterReceiver(networkChangeReceiver);
            }
        }
    }

    private void initValues() {
        if (backgroundRes == 0) {
            backgroundRes = R.drawable.alert_background;
        }
        if (messageText != null && !messageText.equals("")) {
            if (messageTextSize == 0) {
                messageTextSize = (int) getResources().getDimension(R.dimen.alertMessageTextSize);
            }
            if (messageTextColor == 0) {
                messageTextColor = getResources().getColor(R.color.alertMessageTextColor);
            }
        }
        if (closeEnable) {
            if (closeDrawable == null) {
                closeDrawable = getResources().getDrawable(R.drawable.ic_close);
            }
        }
        if (font == null) {
            if (getActivity() != null) {
                font = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.itemFontPath));
            }
        }
        if (alertButtons == null) {
            alertButtons = new ArrayList<>();
        }
        if (alertType == null) {
            alertType = AlertType.ALERT;
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getClass().getName());
    }

    private void setDialogAttribute() {
        clAlertDialog.setBackgroundResource(backgroundRes);
    }

    private void setMessageAttribute() {
        if (messageText != null && !messageText.equals("")) {
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(messageText);
            txtMessage.setTextColor(messageTextColor);
            txtMessage.setTypeface(font);
            if (getContext() != null) {
                TextSizeHelper.changeTextSizeWithDipUnit(getContext(), txtMessage, messageTextSize);
            }
        } else {
            txtMessage.setVisibility(View.GONE);
        }
    }

    private void setButtonAttribute() {
        if (alertButtons == null || alertButtons.size() <= 0) {
            rvButtons.setVisibility(View.GONE);
        } else {
            if (buttonMargin != 0f) {
                if (getContext() != null) {
                    int space = (int) MeasureHelper.pixelToDip(getContext(), buttonMargin);
                    rvButtons.addItemDecoration(new SpaceItemDecoration(space));
                }
            }
            ButtonAdapter buttonAdapter = new ButtonAdapter(getContext(), alertButtons, font, alertClickListener, this);
            if (alertButtons.size() <= maxRowButton) {
                rvButtons.setLayoutManager(new GridLayoutManager(getContext(), alertButtons.size()));
            } else {
                rvButtons.setLayoutManager(new GridLayoutManager(getContext(), maxRowButton));
            }
            rvButtons.setAdapter(buttonAdapter);
            rvButtons.setVisibility(View.VISIBLE);
        }
    }

    private void setCloseAttribute() {
        if (closeEnable) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (alertCloseListener != null) {
                        alertCloseListener.onClick();
                    }
                    dismiss();
                }
            });
            if (closeTintColorRes != 0) {
                if (getContext() != null) {
                    btnClose.setColorFilter(ContextCompat.getColor(getContext(), closeTintColorRes), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
            btnClose.setImageResource(closeDrawableRes);
            btnClose.setVisibility(View.VISIBLE);
        } else {
            btnClose.setVisibility(View.GONE);
        }
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public static class Builder {

        private AlertCloseListener alertCloseListener = null;
        private AlertClickListener alertClickListener = null;
        private double width = 0.85;
        private double height = 0.30;
        private int backgroundRes = 0;
        private String messageText = null;
        private float messageTextSize = 0;
        private int messageTextColor = 0;
        private Drawable closeDrawable = null;
        private int closeDrawableRes = 0;
        private int closeTintColorRes = 0;
        private boolean closeEnable = true;
        private boolean cancelable = true;
        private Typeface font = null;
        private List<AlertButton> alertButtons = null;
        private int maxRowButton = 3;
        private float buttonMargin = 0f;
        private boolean networkReceiverIsEnable = false;
        private NetworkReceiverListener networkReceiverListener = null;
        private boolean animationEnable = false;
        private AlertType alertType = AlertType.ALERT;

        public Builder setAlertCloseClickListener(AlertCloseListener alertCloseListener) {
            this.alertCloseListener = alertCloseListener;
            return this;
        }

        public Builder setAlertClickListener(AlertClickListener alertClickListener) {
            this.alertClickListener = alertClickListener;
            return this;
        }

        public Builder setWidth(double width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(double height) {
            this.height = height;
            return this;
        }

        public Builder setBackgroundRes(int backgroundRes) {
            this.backgroundRes = backgroundRes;
            return this;
        }

        public Builder setMessageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public Builder setMessageTextSize(float messageTextSize) {
            this.messageTextSize = messageTextSize;
            return this;
        }

        public Builder setMessageTextColor(int messageTextColor) {
            this.messageTextColor = messageTextColor;
            return this;
        }

        /**
         * @param closeDrawable drawable
         * @desc set vector drawable  has crash on api pre lollipop
         * @desc please use method setCloseDrawableRes
         */
        @Deprecated
        public Builder setCloseDrawable(Drawable closeDrawable) {
            this.closeDrawable = closeDrawable;
            return this;
        }

        public Builder setCloseDrawableRes(int closeDrawableRes) {
            this.closeDrawableRes = closeDrawableRes;
            return this;
        }

        public Builder setCloseTintColorRes(int closeTintColorRes) {
            this.closeTintColorRes = closeTintColorRes;
            return this;
        }

        public Builder setCloseEnable(boolean closeEnable) {
            this.closeEnable = closeEnable;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setFont(Typeface font) {
            this.font = font;
            return this;
        }

        public Builder setAlertButtons(List<AlertButton> alertButtons) {
            this.alertButtons = alertButtons;
            return this;
        }

        public Builder setMaxRowButton(int maxRowButton) {
            this.maxRowButton = maxRowButton;
            return this;
        }

        public Builder setButtonMargin(float buttonMargin) {
            this.buttonMargin = buttonMargin;
            return this;
        }

        public Builder setNetworkReceiverEnable(boolean networkReceiverIsEnable) {
            this.networkReceiverIsEnable = networkReceiverIsEnable;
            return this;
        }

        public Builder setNetworkReceiverListener(NetworkReceiverListener networkReceiverListener) {
            this.networkReceiverListener = networkReceiverListener;
            return this;
        }

        public Builder setAnimationEnable(boolean animationEnable) {
            this.animationEnable = animationEnable;
            return this;
        }

        public Builder setAlertCloseListener(AlertCloseListener alertCloseListener) {
            this.alertCloseListener = alertCloseListener;
            return this;
        }

        public Builder setNetworkReceiverIsEnable(boolean networkReceiverIsEnable) {
            this.networkReceiverIsEnable = networkReceiverIsEnable;
            return this;
        }

        public Builder setAlertType(AlertType alertType) {
            this.alertType = alertType;
            return this;
        }

        public AlertDialog build() {
            AlertDialog alertDialog = new AlertDialog();
            alertDialog.setCancelable(cancelable);
            alertDialog.alertCloseListener = alertCloseListener;
            alertDialog.alertClickListener = alertClickListener;
            alertDialog.width = width;
            alertDialog.height = height;
            alertDialog.backgroundRes = backgroundRes;
            alertDialog.messageText = messageText;
            alertDialog.messageTextSize = messageTextSize;
            alertDialog.messageTextColor = messageTextColor;
            alertDialog.closeDrawable = closeDrawable;
            alertDialog.closeDrawableRes = closeDrawableRes;
            alertDialog.closeTintColorRes = closeTintColorRes;
            alertDialog.closeEnable = closeEnable;
            alertDialog.font = font;
            alertDialog.alertButtons = alertButtons;
            alertDialog.maxRowButton = maxRowButton;
            alertDialog.buttonMargin = buttonMargin;
            alertDialog.networkReceiverIsEnable = networkReceiverIsEnable;
            alertDialog.networkReceiverListener = networkReceiverListener;
            alertDialog.animationEnable = animationEnable;
            alertDialog.alertType = alertType;
            return alertDialog;
        }

    }

}