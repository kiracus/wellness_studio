package localDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String nickname;

    @ColumnInfo(name = "has_online_account")
    public Boolean hasOnlineAccount;

    @ColumnInfo(name = "email")
    public String email;

}
