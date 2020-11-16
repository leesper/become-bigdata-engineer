package com.bigdata.secondarysort;

import org.apache.hadoop.io.WritableComparable;
import org.testng.annotations.IDataProviderAnnotation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Person implements WritableComparable<Person> {
    private String name;
    private int age;
    private int salary;

    public Person() {}

    public Person(String name, int age, int salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    @Override
    public int compareTo(Person o) {
        if (salary - o.getSalary() != 0) {
            return -(salary - o.getSalary());
        }
        return age - o.getAge();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(name);
        dataOutput.writeInt(age);
        dataOutput.writeInt(salary);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        name = dataInput.readUTF();
        age = dataInput.readInt();
        salary = dataInput.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d\t%d", name, age, salary);
    }
}
