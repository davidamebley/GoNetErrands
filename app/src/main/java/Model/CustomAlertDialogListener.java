package Model;

import android.app.Dialog;
import android.view.View;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class CustomAlertDialogListener implements View.OnClickListener{
    private final Dialog dialog;

    public CustomAlertDialogListener(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {

        // Do whatever you want here

        // If you want to close the dialog, uncomment the line below
        //dialog.dismiss();
    }
}
