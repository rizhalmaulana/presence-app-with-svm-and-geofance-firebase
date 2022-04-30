package ac.id.ubpkarawang.sigeoo.Base;

import androidx.fragment.app.Fragment;

import ac.id.ubpkarawang.sigeoo.Utils.ApiBeritaSource;

public class BaseFragment extends Fragment {
    public ApiBeritaSource getBeritaSource() {
        return new ApiBeritaSource();
    }
}
