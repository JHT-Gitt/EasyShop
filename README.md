# 🛒 EasyShop - E-commerce Web Application

Welcome to **EasyShop**, a full-stack e-commerce web application built using Java Spring Boot, MySQL, and a modern responsive front-end.



## 🚀 Features

- 🛍️ Product browsing by category, color, and price filter
- 🔎 Detailed product view
- 🧑 User registration & login
- 🔐 Secure access with Spring Security
- 🛒 Add to Cart, Update Quantity, and Clear Cart
- ✅ Role-based access control for Admin and User
- 📦 Order management and checkout (optional future scope)

## 🛠️ Technologies Used

- **Backend**: Java, Spring Boot, Spring Security
- **Frontend**: HTML, CSS, Bootstrap, Thymeleaf
- **Database**: MySQL
- **API Testing**: Postman

## 📁 Project Structure

src/ <br>
└── main/ <br>
└── java/org/yearup/ <br>
├── configurations/<br>
│ └── DatabaseConfig.java <br>
├── controllers/ <br>
│ ├── AuthenticationController.java <br>
│ ├── CategoriesController.java <br>
│ ├── ProductsController.java <br>
│ └── ShoppingCartController.java <br>
├── data/  <br>
│ ├── CategoryDao.java <br>
│ ├── ProductDao.java <br>
│ ├── ProfileDao.java <br>
│ ├── ShoppingCartDao.java <br>
│ └── UserDao.java <br>
├── data/mysql/ <br>
│ ├── MySqlCategoryDao.java <br>
│ ├── MySqlDaoBase.java <br>
│ ├── MySqlProductDao.java <br>
│ ├── MySqlProfileDao.java <br>
│ ├── MySqlShoppingCartDao.java <br>
│ └── MySqlUserDao.java <br>
├── models/ <br>
│ ├── authentication/ <br>
│ │ ├── Category.java <br>
│ │ └── Product.java <br>
│ ├── Profile.java <br>
│ ├── ShoppingCart.java <br>
│ ├── ShoppingCartItem.java <br>
│ └── User.java <br>
├── security/<br>
└── EasyshopApplication.java <br>

