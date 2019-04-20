# API Document

## User Login

### Description:

* User Log in interface

### Request URL

* <font color=#DC143C size=3>`http://10.20.35.198:8080/userLogin`</font>

### Request Method

* <font color=#DC143C size=3>`POST`</font>

### Parameters

| Name | Compulsory | Type | Description                       |
| ---- | ---------- | ---- | --------------------------------- |
| user | Yes        | User | User Object (Converted from json) |

### Response Message

```json
{
    "code": 0,
    "message": "Login Success",
    "data": {
        "userName": "Xiao Yue"
    }
}
```

| para_name | Type | Description            |
| --------- | ---- | ---------------------- |
| code      | Int  | 0  `success`  1 `fail` |

## User Signup

### Description:

- User Signup in interface

### Request URL

- <font color=#DC143C size=3>`http://10.20.35.198:8080/userSignup`</font>

### Request Method

- <font color=#DC143C size=3>`POST`</font>

### Parameters

| Name | Compulsory | Type | Description                       |
| ---- | ---------- | ---- | --------------------------------- |
| user | Yes        | User | User Object (Converted from json) |

### Response Message

```json
{
    "code": 0,
    "message": "Signup Success",
    "data": {
        "userName": "Yvette"
    }
}
```

| para_name | Type | Description            |
| --------- | ---- | ---------------------- |
| code      | Int  | 0  `success`  1 `fail` |

## Access Image

Now, I simply put them in `static/img`. Thus, you could use static way to get it.

e.g.

<font color=#DC143C size=3>`http://10.20.35.198:8080/img/cool_cat.jpeg`</font>

I have put the following images in the server.

```
.
└── img
    ├── black_cat.jpeg
    ├── cool_cat.jpeg
    ├── cute_cat1.jpeg
    ├── cute_cat2.jpeg
    ├── desktop.png
    ├── mad_cat.jpeg
    └── small_cat.jpeg
    
```

