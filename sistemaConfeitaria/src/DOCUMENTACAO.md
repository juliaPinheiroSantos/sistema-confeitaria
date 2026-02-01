# 🎂 Documentação do Sistema de Confeitaria

Documentação técnica gerada automaticamente via **Custom Doclet**.

## 📦 Pacote: `entities`

### 📄 Classe: `Address`

| Elemento    | Nome              |
| :---------- | :---------------- |
| 🔹 Atributo | `id`              |
| 🔹 Atributo | `cep`             |
| 🔹 Atributo | `street`          |
| 🔹 Atributo | `number`          |
| 🔹 Atributo | `complement`      |
| 🔹 Atributo | `reference`       |
| 🔹 Atributo | `area`            |
| ⚙️ Método   | `getInteger()`    |
| ⚙️ Método   | `getCep()`        |
| ⚙️ Método   | `setCep()`        |
| ⚙️ Método   | `getStreet()`     |
| ⚙️ Método   | `setStreet()`     |
| ⚙️ Método   | `getNumber()`     |
| ⚙️ Método   | `setNumber()`     |
| ⚙️ Método   | `getComplement()` |
| ⚙️ Método   | `setComplement()` |
| ⚙️ Método   | `getReference()`  |
| ⚙️ Método   | `setReference()`  |
| ⚙️ Método   | `setArea()`       |
| ⚙️ Método   | `getArea()`       |
| ⚙️ Método   | `toString()`      |

---

### 📄 Classe: `AddressException`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `Area`

| Elemento    | Nome         |
| :---------- | :----------- |
| 🔹 Atributo | `id`         |
| 🔹 Atributo | `name`       |
| 🔹 Atributo | `fee`        |
| ⚙️ Método   | `getId()`    |
| ⚙️ Método   | `getName()`  |
| ⚙️ Método   | `setName()`  |
| ⚙️ Método   | `getFee()`   |
| ⚙️ Método   | `setFee()`   |
| ⚙️ Método   | `toString()` |

---

### 📄 Classe: `DbException`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `DeliveryType`

| Elemento  | Nome        |
| :-------- | :---------- |
| ⚙️ Método | `values()`  |
| ⚙️ Método | `valueOf()` |

---

### 📄 Classe: `FlavorLevel`

| Elemento  | Nome        |
| :-------- | :---------- |
| ⚙️ Método | `values()`  |
| ⚙️ Método | `valueOf()` |

---

### 📄 Classe: `Order`

| Elemento    | Nome                    |
| :---------- | :---------------------- |
| 🔹 Atributo | `id`                    |
| 🔹 Atributo | `idUser`                |
| 🔹 Atributo | `dateTime`              |
| 🔹 Atributo | `totalPrice`            |
| 🔹 Atributo | `observations`          |
| 🔹 Atributo | `delivery`              |
| 🔹 Atributo | `orderItems`            |
| ⚙️ Método   | `getId()`               |
| ⚙️ Método   | `getDateTime()`         |
| ⚙️ Método   | `setDateTime()`         |
| ⚙️ Método   | `getTotalPrice()`       |
| ⚙️ Método   | `setTotalPrice()`       |
| ⚙️ Método   | `calculateTotalPrice()` |
| ⚙️ Método   | `getObservations()`     |
| ⚙️ Método   | `setObservations()`     |
| ⚙️ Método   | `getDelivery()`         |
| ⚙️ Método   | `setDelivery()`         |
| ⚙️ Método   | `getOrderItems()`       |
| ⚙️ Método   | `setOrderItems()`       |
| ⚙️ Método   | `addItem()`             |
| ⚙️ Método   | `removeAllSameItems()`  |
| ⚙️ Método   | `removeOneItem()`       |
| ⚙️ Método   | `toString()`            |

---

### 📄 Classe: `OrderItems`

| Elemento    | Nome                 |
| :---------- | :------------------- |
| 🔹 Atributo | `id`                 |
| 🔹 Atributo | `product`            |
| 🔹 Atributo | `quantity`           |
| 🔹 Atributo | `priceAtMoment`      |
| ⚙️ Método   | `getId()`            |
| ⚙️ Método   | `getProduct()`       |
| ⚙️ Método   | `setProduct()`       |
| ⚙️ Método   | `getQuantity()`      |
| ⚙️ Método   | `setQuantity()`      |
| ⚙️ Método   | `getPriceAtMoment()` |
| ⚙️ Método   | `setPriceAtMoment()` |
| ⚙️ Método   | `subtotal()`         |
| ⚙️ Método   | `toString()`         |

---

### 📄 Classe: `Person`

| Elemento    | Nome             |
| :---------- | :--------------- |
| 🔹 Atributo | `id`             |
| 🔹 Atributo | `firstName`      |
| 🔹 Atributo | `lastName`       |
| 🔹 Atributo | `email`          |
| 🔹 Atributo | `address`        |
| ⚙️ Método   | `setId()`        |
| ⚙️ Método   | `getId()`        |
| ⚙️ Método   | `getFirstName()` |
| ⚙️ Método   | `setFirstName()` |
| ⚙️ Método   | `getLastName()`  |
| ⚙️ Método   | `setLastName()`  |
| ⚙️ Método   | `getEmail()`     |
| ⚙️ Método   | `setEmail()`     |
| ⚙️ Método   | `getAddress()`   |
| ⚙️ Método   | `setAddress()`   |
| ⚙️ Método   | `toString()`     |

---

### 📄 Classe: `Product`

| Elemento    | Nome               |
| :---------- | :----------------- |
| 🔹 Atributo | `id`               |
| 🔹 Atributo | `name`             |
| 🔹 Atributo | `description`      |
| 🔹 Atributo | `price`            |
| 🔹 Atributo | `size`             |
| 🔹 Atributo | `flavor`           |
| 🔹 Atributo | `level`            |
| ⚙️ Método   | `setId()`          |
| ⚙️ Método   | `getId()`          |
| ⚙️ Método   | `getName()`        |
| ⚙️ Método   | `setName()`        |
| ⚙️ Método   | `getDescription()` |
| ⚙️ Método   | `setDescription()` |
| ⚙️ Método   | `getFlavor()`      |
| ⚙️ Método   | `setFlavor()`      |
| ⚙️ Método   | `getLevel()`       |
| ⚙️ Método   | `setLevel()`       |
| ⚙️ Método   | `getSize()`        |
| ⚙️ Método   | `setSize()`        |
| ⚙️ Método   | `getPrice()`       |
| ⚙️ Método   | `setPrice()`       |
| ⚙️ Método   | `toString()`       |

---

### 📄 Classe: `Size`

| Elemento    | Nome          |
| :---------- | :------------ |
| ⚙️ Método   | `values()`    |
| ⚙️ Método   | `valueOf()`   |
| 🔹 Atributo | `id`          |
| 🔹 Atributo | `yield`       |
| 🔹 Atributo | `weight`      |
| ⚙️ Método   | `getId()`     |
| ⚙️ Método   | `getYield()`  |
| ⚙️ Método   | `getWeight()` |

---

### 📄 Classe: `User`

| Elemento    | Nome                |
| :---------- | :------------------ |
| 🔹 Atributo | `idUser`            |
| 🔹 Atributo | `passwordHash`      |
| ⚙️ Método   | `setIdUser()`       |
| ⚙️ Método   | `getIdUser()`       |
| ⚙️ Método   | `getPasswordHash()` |
| ⚙️ Método   | `toString()`        |

---

## 📦 Pacote: `repositories`

### 📄 Classe: `CreateTables`

| Elemento  | Nome                      |
| :-------- | :------------------------ |
| ⚙️ Método | `createTablePerson()`     |
| ⚙️ Método | `createTableUser()`       |
| ⚙️ Método | `createTableArea()`       |
| ⚙️ Método | `createTableAddress()`    |
| ⚙️ Método | `createTableOrder()`      |
| ⚙️ Método | `createTableProduct()`    |
| ⚙️ Método | `createTableOrderItems()` |

---

### 📄 Classe: `DBConnection`

| Elemento    | Nome              |
| :---------- | :---------------- |
| 🔹 Atributo | `ADDRESS_IP`      |
| 🔹 Atributo | `ADDRESS_PORT`    |
| 🔹 Atributo | `NAME_DATABASE`   |
| 🔹 Atributo | `USER`            |
| 🔹 Atributo | `PASSWORD`        |
| ⚙️ Método   | `getConnection()` |

---

### 📄 Classe: `RepositoryPerson`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `RepositoryProduct`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `RepositoryUser`

| Elemento    | Nome           |
| :---------- | :------------- |
| 🔹 Atributo | `insertUser`   |
| ⚙️ Método   | `createUser()` |

---

## 📦 Pacote: `services`

### 📄 Classe: `EncryptionService`

| Elemento    | Nome              |
| :---------- | :---------------- |
| 🔹 Atributo | `ALGORITHM`       |
| 🔹 Atributo | `ITERATIONS`      |
| 🔹 Atributo | `KEY_LENGTH`      |
| ⚙️ Método   | `hashPassword()`  |
| ⚙️ Método   | `checkPassword()` |
| ⚙️ Método   | `generateHash()`  |

---

## 📦 Pacote: `app`

### 📄 Classe: `Main`

| Elemento  | Nome     |
| :-------- | :------- |
| ⚙️ Método | `main()` |

---
