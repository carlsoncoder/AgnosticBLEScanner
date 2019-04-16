package helpers;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

public class GenericHelper {

    public static void alertUser(Context context, String message, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    public static int getPrimaryColor() {
        return Color.parseColor("#009973");
    }

    public static int getSecondaryColor() {
        return Color.parseColor("#E1EAEA");
    }
}
