package tamirmo.shopper.Receipt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;

public class ReceiptFragment extends FragmentWithUpdates {

    // Class list adapter
    private ReceiptListAdapter receiptListAdapter;

    // Class widgets
    private ListView receiptListView;
    private TextView totalSumTextView;
    private FrameLayout mainFrame;
    private LinearLayout receiptLayout;
    private LinearLayout loadingLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<CartItem> cart = ((MainActivity)getActivity()).getCart();
        List<Product> products = ((MainActivity)getActivity()).getProducts();
        List<Sale> sales = ((MainActivity)getActivity()).getSales();
        List<Discount> discounts = ((MainActivity)getActivity()).getDiscounts();
        receiptListAdapter = new ReceiptListAdapter(getActivity(), cart, products, discounts, sales);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.receipt_fragment, container, false);

        // Gets the widgets on the fragment
        mainFrame = rootView.findViewById(R.id.main_layout);
        receiptLayout = rootView.findViewById(R.id.receipt_layout);
        loadingLayout = rootView.findViewById(R.id.loading_layout);

        receiptListView = rootView.findViewById(R.id.receipt_list_view);
        receiptListView.setAdapter(receiptListAdapter);

        totalSumTextView = rootView.findViewById(R.id.receipt_total_sum_text_view);
        totalSumTextView.setText(String.format("%.2f $", receiptListAdapter.calculateTotalSum()));

        showReceiptLayout();

        return rootView;
    }

    // Shows login screen
    private void showReceiptLayout(){
        mainFrame.bringChildToFront(receiptLayout);
        receiptLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    // Shows loading screen
    private void showLoadingLayout(){
        mainFrame.bringChildToFront(loadingLayout);
        receiptLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    // Returns a HashMap from a List of products
    public HashMap<String, Product> getProductMap(List<Product> productsList) {
        HashMap<String, Product> productMap = new HashMap<String, Product>();

        for(Product product: productsList){
            productMap.put(product.getProductId(), product);
        }

        return productMap;
    }

    @Override
    public void updateFragment() {
        // Nothing to do
    }

    // Sends the receipt
    public void sendReceipt(){
        SendReceiptTask task = new SendReceiptTask();
        task.execute();
    }

    // AsyncTask is used because main thread can't be used for communication with the server
    private class SendReceiptTask extends AsyncTask<Void, Void, String> {
        String receipt, result;

        // Switches to loading layout
        protected void onPreExecute(){
            // Shows the loading screen while the message is being sent to the server
            showLoadingLayout();

            // Creates a receipt
            receipt = receiptListAdapter.getReceipt();
        }

        // Sends receipt to the server
        // Can't change UI because it isn't the main thread
        protected String doInBackground(Void... voids){
            String result = "";

            try{
                if(((MainActivity)getActivity()).sendReceiptRequest(receipt))
                    result = getString(R.string.receipt_sent_dialog);
                else
                    result = getString(R.string.receipt_sent_dialog_err);
            }catch(Exception e){
                result = e.getMessage();
            }

            return result;
        }

        // Deals with the result of the login request
        protected void onPostExecute(String result) {
            // Changes back to login screen
            showReceiptLayout();

            if(!result.equals(getString(R.string.receipt_sent_dialog))) {
                // Notifies an error occurred
                ((MainActivity)getActivity()).popUpMessageDialog(result);
            }
            else{
                ((MainActivity)getActivity()).sendNotification(getString(R.string.receipt_notification_ticker), getString(R.string.receipt_notification_title),getString(R.string.receipt_notification_message));
            }
        }
    }
}
