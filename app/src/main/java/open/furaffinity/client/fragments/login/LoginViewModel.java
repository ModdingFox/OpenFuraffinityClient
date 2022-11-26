package open.furaffinity.client.fragments.login;

import java.util.Objects;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class LoginViewModel extends ViewModel {
    private final MutableLiveData<String> password;
    private final MutableLiveData<String> userName;

    public LoginViewModel() {
        this.password = new MutableLiveData<>();
        this.userName = new MutableLiveData<>();
    }

    public String getPassword() {
        return Objects.toString(this.password.getValue(), "");
    }

    public String getUserName() {
        return Objects.toString(this.userName.getValue(), "");
    }

    public void setPassword(String value) {
        this.password.setValue(value);
    }

    public void setUserName(String value) {
        this.userName.setValue(value);
    }
}
