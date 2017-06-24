package com.wscubetech.seovideotutorials.Urls;

import com.wscubetech.seovideotutorials.utils.Constants;

public class Urls {

    //360DigiGyan
    //MAIN_URL:- http://www.360digitalgyan.com/api

    public static final String mainUrl = "http://www.360digitalgyan.com/api/";

    public static final String imageUrl = "http://www.360digitalgyan.com/uploads/";

    public static final String viewVideoList = mainUrl + "video.php";
    //course_id= //video_id=
    //language= //1 for English  //2 for Hindi
    //&sub_category_id=3

    public static final String viewVideoCountAll = mainUrl + "video_count.php";


    public static final String viewSubCategoriesIq = mainUrl + "view_sub_cat.php?";
    //category_id=30
    //question_type=  //1->Interview ques  2->Technical Terms

    public static final String viewSubCategoriesQuiz = mainUrl + "quiz_sub_cat.php?";
    //category_id=30

    public static final String viewSubCategoriesStudyMaterial = mainUrl + "study_sub_cat.php?";
    //category_id=30

    public static final String viewSubCategoriesVideos = mainUrl + "video_sub_cat.php?";
    //category_id=30

    public static final String viewQuizTestQuestionsNew = mainUrl + "quiz_question.php?";
    //sub_category_id=9
    //code_flag=0-> center align,  1->left align

    public static final String viewQuizTestQuestionsNewAllTest = mainUrl + "quiz_question_all.php?";
    //sub_category_id=9
    //code_flag=0-> center align,  1->left align

    public static final String viewInterviewQues = mainUrl + "interview_question.php";
    //cat_id=30&page_no=1&question_type=//1->Interview Ques   2->Technical Terms

    public static final String viewStudyMaterialQues = mainUrl + "study_material_questions.php?";
    //cat_id=30&page_no=1&sub_cat_id=3

    public static final String viewStudyMaterialAns = mainUrl + "study_material_detail.php?";
    //study_material_id=2

    public static final String viewSeoPapersTest = mainUrl + "view_paper.php";
    //courses_id= //Constants.SEO_ID;

    public static final String viewQuizQuestions = mainUrl + "view_question.php";
    //paper_id=

    public static final String sendResultToEmail = mainUrl + "add_seo_result.php";
    //paper_title=&email_id=&result=


    public static final String addRegId = mainUrl + "add_seo_reg_id.php";
    //user_id=1&did=&notification_status=1  //1-> for notifying on    0-> notifying off
    //flag=> 1 for Digital Marketing

    public static final String VIEW_PLACED_STUDENTS = "http://www.wscubetech.com/api/" + "view_placed_student.php";

    public static final String SEND_QUERY_CONTACT_US = mainUrl + "contact.php?";
    //name=&email=&phone=&message=&website=&subject=

    public static final String SEND_QUERY_TRAINING_ENQUIRY = mainUrl + "training_enquiry.php";
    //name=&email=&phone=&message=&training_type=&selecttraining=

    public static final String VIEW_NOTIFICATIONS = mainUrl + "view_notification.php";

    public static final String SEND_SUGGESTION = mainUrl + "add_suggestion.php?";
    //user_name=sandeep&suggestion_message=hello


    //Login and Ques/Ans starts from here
    public static final String LOGIN = mainUrl + "seo_user_login.php?";
    //email=s@gmail.com&password=123
    //flag=0  0->normal login    1->Google login (send user name in key 'name' and no password)
    //app_user= 1-> for Digital Marketing app

    public static final String REGISTER = mainUrl + "seo_user_register.php?";
    //name=sandeep&email=s@gmail.com&password=123
    //app_user= 1-> for Digital Marketing app

    public static final String VIEW_PROFILE = mainUrl + "seo_user_profile.php?";
    //seo_users_id=4

    public static final String CHANGE_PASSWORD = mainUrl + "user_change_password.php?";
    //user_id=3&current_pass=123&new_pass=1234
    //flag=1 (1-> if password is blank (google log in)) (blank-> if password exists)

    public static final String FORGOT_PASSWORD = mainUrl + "user_forgot_pass.php?";
    //email=wscubetech@gmail.com
    //app_user= 1-> for Digital Marketing app

    public static final String ADD_QUESTION = mainUrl + "add_question.php?";
    //user_question=hello&ques_user_id=1&ques_main_cat_id=30
    // &question_tags= (comma separated tag titles)

    public static final String EDIT_QUESTION = mainUrl + "edit_posted_question.php?";
    //ques_id=&user_question=&ques_user_id=&ques_main_cat_id=
    // &question_tags= (comma separated tag titles)

    public static final String DELETE_QUESTION=mainUrl+"delete_question.php?";
    //question_id=

    public static final String ADD_ANSWER = mainUrl + "add_answer.php?";
    //ques_answer=yes&ans_user_id=4&ans_ques_id=1

    public static final String EDIT_ANSWER = mainUrl + "edit_posted_answer.php?";
    //answer_id=&ques_answer=&ans_user_id=&ans_ques_id=

    public static final String DELETE_ANSWER = mainUrl + "delete_answer.php?";
    //answer_id=

    public static final String VIEW_POSTED_QUESTIONS = mainUrl + "view_posted_question.php?";
    //user_id=1&ques_main_cat_id=30
    // &limit=1 (no. of ques in one page, by default 25)
    // &page_no=
    //&flag=1  (1->  for my posted questions, blank for all questions)
    /*&flag=>
    1 -> Trending (Default)
    2 -> Most Popular
    3 -> Most Recent
    4 -> Most Answered
    5 -> Unanswered
    6 -> My Questions (user_id)
    7 -> My Answers (user_id)
    */
    //filter_tags => (comma separated tags) (empty by default)
    //liked=> 0->nothing,  1->liked   2->Disliked


    public static final String VIEW_TAGS = mainUrl + "view_newtag.php?";
    //tag_main_cat_id=30

    public static final String VIEW_POSTED_ANSWERS = mainUrl + "view_posted_answer.php?";
    //ans_ques_id=1&user_id=

    public static final String LIKE_DISLIKE_QUES_ANS = mainUrl + "like_dislike.php?";
    //question_answer_id=&user_id=&like_dislike_status=&like_dislike_flag=
    //like_dislike_flag => 1-> question id,    2-> answer id
    //question_answer_id => either ques_id or answer_id
    //like_dislike_status => 0->Default,   1->Like,    2->Dislike

    public static final String VIEW_QUES_COUNT_INCREMENT = mainUrl + "question_view_count.php?";
    //ques_id=5


    public static final String EDIT_PROFILE = mainUrl + "edit_user_profile.php?";
    //user_id=1&user_name=sandeep&user_photo=

    public static final String VIEW_GREETING = mainUrl + "view_greeting.php";

}
