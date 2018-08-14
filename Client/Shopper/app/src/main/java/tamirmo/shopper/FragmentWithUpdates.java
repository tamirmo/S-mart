package tamirmo.shopper;


import android.support.v4.app.Fragment;

// The top Parent fragment in our polymorphism
// Each Son is a fragment that can be called to update itself from an outside source
public abstract class FragmentWithUpdates extends Fragment {
    abstract public void updateFragment();
}
