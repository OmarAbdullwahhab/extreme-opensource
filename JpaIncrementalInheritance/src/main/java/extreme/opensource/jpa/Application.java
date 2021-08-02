/*
 * The MIT License
 *
 * Copyright 2021 Omar Rashwan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package extreme.opensource.jpa;

import extreme.opensource.jpa.entities.Employee;
import extreme.opensource.jpa.entities.SystemUser;
import extreme.opensource.jpa.entities.UserBasicInfo;
import extreme.opensource.jpa.orm.SqlGenerator;

/**
 * 
 * @author Omar Rashwan
 * @date : 02-08-2021 12:00 AM
 */
public class Application {

    public static void main(String args[]) throws Exception
    {
        SqlGenerator gen = new SqlGenerator();
        gen.GenerateSQL(SystemUser.class);
        gen.GenerateSQL(Employee.class);
        gen.GenerateSQL(UserBasicInfo.class);
        
        String incr = gen.generateDDLSQL();
        System.out.println("Generated DDL \n" + incr);
        
        incr = gen.generateSelect(SystemUser.class);
        System.out.println("SystemUser select \n" + incr);
        
        incr = gen.generateSelect(Employee.class);
        System.out.println("Employee select \n" + incr);
        
        incr = gen.generateSelect(UserBasicInfo.class);
        System.out.println("UserBasicInfo select \n" + incr);
        
    }
}
