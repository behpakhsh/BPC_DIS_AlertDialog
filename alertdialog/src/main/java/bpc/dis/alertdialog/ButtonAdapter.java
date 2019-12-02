package bpc.dis.alertdialog;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bpc.dis.utilities.TextSizeHelper.TextSizeHelper;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.MyViewHolder> {

    private List<AlertButton> alertButtons;
    private Context context;
    private Typeface font;
    private AlertClickListener alertClickListener;

    ButtonAdapter(Context context, List<AlertButton> alertButtons, Typeface font, AlertClickListener alertClickListener) {
        this.alertButtons = alertButtons;
        this.context = context;
        this.font = font;
        this.alertClickListener = alertClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_button, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int listPosition) {
        final AlertButton alertButton = alertButtons.get(listPosition);
        holder.btnDialog.setText(alertButton.getButtonText());
        holder.btnDialog.setTypeface(font);
        holder.btnDialog.setBackgroundResource(alertButton.getButtonBackgroundRes());
        if (alertButton.getButtonTextColor() != 0) {
            holder.btnDialog.setTextColor(alertButton.getButtonTextColor());
        }
        if (alertButton.getButtonTextSize() != 0) {
            TextSizeHelper.changeTextSizeWithDipUnit(context, holder.btnDialog, alertButton.getButtonTextSize());
        }
        holder.btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertClickListener != null) {
                    alertClickListener.onClick(alertButton.getTag());
                }
                if (alertButton.getAlertClickListener() != null) {
                    alertButton.getAlertClickListener().onClick(alertButton.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertButtons.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatButton btnDialog;

        MyViewHolder(View itemView) {
            super(itemView);
            btnDialog = itemView.findViewById(R.id.btn_dialog);
        }

    }

}