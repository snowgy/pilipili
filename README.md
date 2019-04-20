# PiliPili

![pilipili logo](https://ws2.sinaimg.cn/large/006tKfTcgy1g1kqwsbmlaj3074074q3v.jpg)

## Team Members

* 11510598 张淮化清
* 11610701 周颖
* 11611722 钟兆玮
* 11611908 龚玥
* 11612831 关浩

## Three Features We have Chosen

### Login/Signup
Registration and login features are an essential part of the community. Users sign up to become a member of the Pilipili community, then log in t upload their own created photos or videos for sharing in the community or view and like photos or videos shared by others. Moreover, the feature is independent of the main function of Pilipili: photo or video is recreated through art filters. In addition, the constructing test cases of this feature is easy. Since most apps have this feature, there are a lot of test cases written by developers for reference and learning.
### Album

Album feature has been chosen since it is one of the important parts of Philip's main function. Pilipili belongs to a photographing tool, it should be able to display the users' album. We have roughly implemented the feature of viewing a photo album. The ease of constructing test cases also becomes the reason why we choose it because neatly and successfully display the pictures is what we concern. 

### File Description

Great masterpiece are indispensable to the stories and creative ideas behind the works. When users are attracted by the work, they need to know more information including author's description and other users' comments of it. The above description gives us the key reason for choosing this feature. File description feature is implemented as a page, cases are constructed to test whether page resources are able to show successfully and back to Home page.  

## CheckStyle, FindBugs, PMD Check

* Server Side Code

  The server side uses Spring-boot Framework in IDEA. 

  - [x] Checkstyle: no serious error (severity="error")
  - [x] PMD: no error left
  - [x] FindBugs: No error with priority="Medium" and priority="High"

* Client Side Code

  - [ ] Checkstyle: no serious error (severity="error")
  - [ ] PMD: no error left
  - [ ] FindBugs: No error with priority="Medium" and priority="High"

## Test Scenario

### Login/Signup
#### Scenario 1:
When user login with an incorrect username/password, the user will get an error message saying “Login failed.”
#### Scenario 2:
When the user login with correct username and password, the user will get a page showing "Authenticating..."
#### Scenario 3:
When user signup with exists username, the user will get an error message saying "Signup failed."
#### Scenario 4:
When user signup with password input length is less than 4 or larger than 10, the user will get a password input error message saying "between 4 and 10 alphanumeric characters."
When user signup with re-enter password not equal to password, the user will get a password input error message saying "Password does not match."



### Album
#### Scenario 1:
When the user successfully login, the user will get an album page showing photos or videos. Randomly click a photo to view its description.
#### Scenario 2:
The album page can be easily wrap up and down. Drag to the bottom of the page and click last several photos to check their description.

### File Description
#### Scenario 1:
When a user clicks a photo, the description page of the photo will successfully display.
#### Scenario 2:

When a user clicks the Home button at the bottom of the page, the user is able to back to the Home page of Pilipili successfully.

## Schedule

| Week 10     | Upload a photo/video, Save photo to local photo album        |
| :---------- | ------------------------------------------------------------ |
| **Week 11** | Take a picture using a camera, Replace image background...   |
| **Week 12** | Photo style transfer, Provide various filters for photos...  |
| **Week 13** | Share photos in community, Thumb up a photo or video in the community... |
| **Week 14** | Test, Upload to Andriod app store...                         |
| **Week 15** | Review issues, Fix bugs, Add new features...                 |