public class Practice {
    public static void main(String[] args){

        Animal dog = new Dog("Dog");
        Animal cat = new Cat("Cat");

        dog.move();
        dog.sound();
        cat.sound();
    }
}


abstract class Animal{
    private String name;

    public Animal(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void move(){
        System.out.println(getName() + "在移动");
    }

    public abstract void sound();
}

class Dog extends Animal{
    public Dog(String name){
        super(name);
    }

    @Override
    public void sound(){
        System.out.println(getName() + "汪汪叫");
    }
}

class Cat extends Animal{
    public Cat(String name){
        super(name);
    }

    @Override
    public void sound(){
        System.out.println(getName() + "喵喵叫");
    }
}