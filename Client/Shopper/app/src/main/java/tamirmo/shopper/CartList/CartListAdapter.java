package tamirmo.shopper.CartList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.R;

public class CartListAdapter extends ArrayAdapter<CartItem> implements View.OnClickListener{

    // Class listener
    private IOnListChange dataChangedListener;

    // Class attributes
    private Context context;
    private List<CartItem> cart;
    private List<Product> products;
    private List<Discount> discounts;
    private List<Sale> sales;
    private HashMap<String, Product> productMap;
    private HashMap<String, Discount> discountMap;
    private HashMap<String, Sale> saleMap;

    // Sets a click listener on the adapter
    public void setDataChangedListener(IOnListChange dataChangedListener) {
        this.dataChangedListener = dataChangedListener;
    }

    CartListAdapter(Context context, List<CartItem> cart, List<Product> products, List<Discount> discounts, List<Sale> sales) {
        super(context, R.layout.cart_item_layout, new CartItem[0]);
        this.context = context;

        this.products = products;
        this.discounts = discounts;
        this.sales = sales;
        this.cart = cart;

        productMap = getProducts(products);
        discountMap = getDiscounts(discounts);
        saleMap = getSales(sales);
    }

    @Override
    public int getCount() {
        return cart.size();
    }

    @Nullable
    @Override
    public CartItem getItem(int position) {
        return cart.get(position);
    }

    @NonNull
    @Override
    // getView is called several times automatically, to fill the list
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CartItem cartItem = getItem(position);
        Product product = productMap.get(cartItem.getProductID());
        Discount discount = discountMap.get(product.getProductId());

        // Creates a view holder for current product
        ProductAdapterUtilities.ProductViewObjects productViewObjects = ProductAdapterUtilities.createViewHolder(convertView, parent, context);
        ProductAdapterUtilities.ProductItemViewHolder viewHolder = productViewObjects.productItemViewHolder;
        ProductAdapterUtilities.setProductData(product, viewHolder, context);

        if(discount != null){
            viewHolder.price.setText(String.format("$%s", discount.getDiscountedPrice()));
        }
        else if(product != null) {
            viewHolder.price.setText(String.format("$%s", product.getPricePerUnit()));
        }

        viewHolder.amount.setText(String.valueOf(cartItem.getAmount()));
        viewHolder.plus.setOnClickListener(this);
        viewHolder.minus.setOnClickListener(this);
        viewHolder.plus.setTag(position);
        viewHolder.minus.setTag(position);
        viewHolder.amount.setVisibility(View.VISIBLE);
        viewHolder.plus.setVisibility(View.VISIBLE);

        if(!cartItem.getIsPicked()) {
            viewHolder.minus.setVisibility(View.VISIBLE);
            viewHolder.pickedImage.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.minus.setVisibility(View.INVISIBLE);
            viewHolder.pickedImage.setVisibility(View.VISIBLE);
        }

        // Return the completed view to render on screen
        return productViewObjects.convertView;
    }

    @Override
    public void onClick(View v) {
        // Checks which button was clicked
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        CartItem dataModel = (CartItem)object;

        ProductAdapterUtilities.onPlusMinusClicked(v, dataModel, context, cart);
        dataChangedListener.updateTotalSum(calculateTotalSum());

        // Refresh the slot that its buttons were clicked
        this.notifyDataSetChanged();
    }

    // Creates HashMap from a list of products
    private HashMap<String, Product> getProducts(List<Product> productsList){
        HashMap<String, Product> products = new HashMap<String, Product>();

        for(Product product : productsList){
            products.put(product.getProductId(), product);
        }

        return products;
    }

    // Creates HashMap from a list of discounts
    private HashMap<String, Discount> getDiscounts(List<Discount> discountsList){
        HashMap<String, Discount> discounts = new HashMap<String, Discount>();

        for(Discount discount : discountsList){
            discounts.put(discount.getProductId(), discount);
        }

        return discounts;
    }

    // Creates HashMap from a list of sales
    private HashMap<String, Sale> getSales(List<Sale> salesList){
        HashMap<String, Sale> sales = new HashMap<String, Sale>();

        for(Sale sale : salesList){
            sales.put(sale.getProductID(), sale);
        }

        return sales;
    }

    // Calculates total sum of list
    public double calculateTotalSum() {
        double sum = 0;

        for ( CartItem item : cart){
            String productID = item.getProductID();
            int amount = item.getAmount();
            Sale sale = saleMap.get(productID);
            Discount discount = discountMap.get(productID);
            Product product = productMap.get(productID);
            int payAmount = 0;

            // Calculates how many products need to be paid
            if(sale != null) {
                int salePayAmount = sale.getPayAmount();
                int saleFreeAmount = sale.getFreeAmount();
                int completedSales = amount / (salePayAmount + saleFreeAmount);
                amount -= completedSales;
                payAmount = completedSales;
                while (amount > 0){
                    for (int i = salePayAmount; i > 0 && amount > 0; i--) {
                        payAmount++;
                        amount--;
                    }
                for (int i = saleFreeAmount; i > 0 && amount > 0; i--) {
                    amount--;
                    }
                }
                amount = payAmount;
            }

            // Updates total price
            if(discount != null) {
                sum += amount * Double.parseDouble(discount.getDiscountedPrice());
            }
            else{
                sum += amount * Double.parseDouble(product.getPricePerUnit());
            }
        }

        return sum;
    }

    // Updates the class data
    public void updateData(){
        productMap = getProducts(products);
        discountMap = getDiscounts(discounts);
        saleMap = getSales(sales);

        dataChangedListener.updateTotalSum(calculateTotalSum());

        // Changes the view based on the new data
        this.notifyDataSetChanged();
    }
}
