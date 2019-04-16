package listadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.agnosticblescanner.R;
import datamodel.BeaconModel;
import helpers.GenericHelper;

public class CustomBeaconListAdapter extends ArrayAdapter<BeaconModel> {

    //region Fields

    private final Activity context;
    private final BeaconModel[] beacons;

    //endregion

    //region Constructors

    public CustomBeaconListAdapter(Activity context, BeaconModel[] beaconRecords)
    {
        super(context, R.layout.custom_beacon_list, beaconRecords);
        this.context = context;
        this.beacons = beaconRecords;
    }

    //endregion

    //region ArrayAdapter<BeaconModel> implementation

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (view == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = this.context.getLayoutInflater();
            view = inflater.inflate(R.layout.custom_beacon_list, null, true);
            viewHolder.majorTextView = view.findViewById(R.id.majorText);
            viewHolder.minorTextView = view.findViewById(R.id.minorText);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }

        if (position % 2 == 1)
        {
            view.setBackgroundColor(GenericHelper.getPrimaryColor());
        }
        else
        {
            view.setBackgroundColor(GenericHelper.getSecondaryColor());
        }

        BeaconModel current = this.beacons[position];

        viewHolder.majorTextView.setText(String.format("Major: %d", current.getMajor()));
        viewHolder.minorTextView.setText(String.format("Minor: %d", current.getMinor()));
        return view;
    }

    //endregion

    //region Embedded Classes

    private static class ViewHolder
    {
        TextView majorTextView;
        TextView minorTextView;
    }

    //endregion
}