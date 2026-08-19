// ============================================================
// GenericInterfaceDemo.java — 泛型接口
// ============================================================

// 泛型接口：一个"数据仓库"，能按 id 查、能存
interface Repository<T> {
    T findById(int id);    // 返回 T
    void save(T item);     // 存 T
}

// 实现类：implements Repository<User>，把 T 填成 User
//   从此这个仓库只装 User，编译器强制保证
class UserRepository implements Repository<User> {
    public User findById(int id) {
        return new User(id, "小明");
    }

    public void save(User item) {
        System.out.println("保存用户: " + item);
    }
}

class User {
    int id;
    String name;

    User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return id + ":" + name;
    }
}

public class GenericInterfaceDemo {
    public static void main(String[] args) {
        // 接口类型也用泛型：声明 repo 是"装 User 的仓库"
        Repository<User> repo = new UserRepository();

        User u = repo.findById(1);   // 返回 User，不用强转
        System.out.println("查到: " + u);

        repo.save(u);
    }
}
