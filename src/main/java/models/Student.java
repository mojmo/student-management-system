package models;

public class Student {
    private String id;
    private String name;
    private String email;
    private int age;
    private String course;
    private double gpa;
    public static final String FILE_HEADER = "ID,NAME,EMAIL,AGE,COURSE,GPA";

    public Student (String id, String name, String email, int age, String course, double gpa) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.course = course;
        this.gpa = gpa;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getCourse() {
        return course;
    }

    public double getGpa() {
        return gpa;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail(int age) {
        this.age = age;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + email + "," + age + "," + course + "," + gpa;
    }
}
