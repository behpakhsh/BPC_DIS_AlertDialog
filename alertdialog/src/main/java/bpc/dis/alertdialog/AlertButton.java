package bpc.dis.alertdialog;

public class AlertButton {

    private AlertClickListener alertClickListener;
    private int buttonBackgroundRes;
    private String buttonText;
    private float buttonTextSize;
    private int buttonTextColor;
    private String tag;

    public String getButtonText() {
        return buttonText;
    }

    public float getButtonTextSize() {
        return buttonTextSize;
    }

    public int getButtonTextColor() {
        return buttonTextColor;
    }

    public int getButtonBackgroundRes() {
        return buttonBackgroundRes;
    }

    public String getTag() {
        return tag;
    }

    public AlertClickListener getAlertClickListener() {
        return alertClickListener;
    }

    public static class Builder {

        private AlertClickListener alertClickListener = null;
        private int buttonBackgroundRes = R.color.defaultColor;
        private String buttonText = "";
        private float buttonTextSize = 0;
        private int buttonTextColor = 0;
        private String tag = "";

        public Builder setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public Builder setButtonTextSize(float buttonTextSize) {
            this.buttonTextSize = buttonTextSize;
            return this;
        }

        public Builder setButtonTextColor(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        public Builder setButtonBackgroundRes(int buttonBackgroundRes) {
            this.buttonBackgroundRes = buttonBackgroundRes;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setAlertClickListener(AlertClickListener alertClickListener) {
            this.alertClickListener = alertClickListener;
            return this;
        }

        public AlertButton build() {
            AlertButton alertButton = new AlertButton();
            alertButton.buttonBackgroundRes = buttonBackgroundRes;
            alertButton.buttonText = buttonText;
            alertButton.buttonTextSize = buttonTextSize;
            alertButton.buttonTextColor = buttonTextColor;
            alertButton.tag = tag;
            alertButton.alertClickListener = alertClickListener;
            return alertButton;
        }

    }

}
