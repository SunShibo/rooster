function validate() {
    var indicator = true;
    var username = $.trim($("#username").val());
    var password = $("#password").val();

    if (username === "") {
        indicator = false;
        $("#username").addClass("is-error");
    }
    if (password === "") {
        indicator = false;
        $("#password").addClass("is-error");
    }

    if (!indicator) {
        $("#etc-login-error-empty").show();
    }
    return indicator;
}
//function isNotExist() {
//    var query = window.location.search.substr(1).split("&");
//    return $.inArray("auth=false", query) > -1;
//}
//
//function isSuspendedOrExpired() {
//    var query = window.location.search.substr(1).split("&");
//    return $.inArray("expisusp=true", query) > -1;
//}

function getArgs( ) {
    var args = {};
    var query = location.search.substring(1);
    var pairs = query.split("&");
    for(var i = 0; i < pairs.length; i++) {
        var pos = pairs[i].indexOf('=');
        if (pos == -1) continue;
        var argname = pairs[i].substring(0,pos);
        var value = pairs[i].substring(pos+1);
        value = decodeURIComponent(value);
        args[argname] = value;
    }
    return args;
}

function isValidEmail(email){
    var RE = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return RE.test(email);
}

function getCookie(c_name)
{
var c_value = document.cookie;
var c_start = c_value.indexOf(" " + c_name + "=");
if (c_start == -1)
  {
  c_start = c_value.indexOf(c_name + "=");
  }
if (c_start == -1)
  {
  c_value = null;
  }
else
  {
  c_start = c_value.indexOf("=", c_start) + 1;
  var c_end = c_value.indexOf(";", c_start);
  if (c_end == -1)
  {
c_end = c_value.length;
}
c_value = unescape(c_value.substring(c_start,c_end));
}
return c_value;
}


$(function () {


    $(".etc-login-input, .etc-login-intercept-input").placeholder();
//    if (isNotExist())
//        $("#etc-login-error-server").css('display', 'block');
//
//    if (isSuspendedOrExpired()) {
//        $("#etc-login-error-expisusp").css('display', 'block');
//    }

    $("#etc-login-form").submit(function () {
        $("#etc-login-error-server").hide();
        $("#etc-login-error-expisusp").hide();
        $("#username, #password").removeClass("is-error");
        if (validate()) {
            this.submit();
        }
        return false;
    });

    //dialog
 //   $("#forgot-password").fancybox({
  //      "overlayShow": true,
  //      "showCloseButton": true,
  //      "hideOnOverlayClick": false,
  //      "enableEscapeButton": false,
  //      "onStart": function () {
   //         document.getElementById("captcha").src = "/services/oboe2/captcha/captchaimage?" + new Date().getTime();
   //         document.getElementById("etc-login-intercept-form").reset();
   //         $("#etc-login-intercept-form .etc-login-error-msg").hide();
//
  //      },
  //      "onComplete": function () {
  //          document.getElementById("input-email").focus();
 // //          document.getElementById("input-captcha").focus();
 //           document.getElementById("input-captcha").blur();
 //       }
 //   });


    //Authentication
    $(".etc-login-captcha-refresh").click(function () {
        document.getElementById("captcha").src = "/services/oboe2/captcha/captchaimage?" + new Date().getTime();
        $("#etc-login-error-captcha").hide();
    })

    //confirm action
    $("#confirm-btn").click(function () {
        var confirmBtn = $(this);
        if (confirmBtn.data("isLoading")) {
            return false;
        }
        $("#etc-login-intercept-form .etc-login-error-msg").hide();
        var email = $.trim($("#input-email").val());
        var captcha = $.trim($("#input-captcha").val());

        if (!isValidEmail(email)) {
            $("#etc-login-error-email").show();
            return false;
        }
        if (captcha == "") {
            $("#etc-login-error-captcha-empty").show();
            return false;
        }

        $.fancybox.showActivity();
        confirmBtn.data("isLoading", true);
        $("#ecforgetpwdtoken").val(getCookie("ecforgetpwd_verify"));
        $.ajax({
            url: "/services/oboe2/Captcha/CaptchaImage/ForgetPassword",
            type: "POST",
            data: $("#etc-login-intercept-form").serialize(),
            success: function (response) {
                if (response && response.IsSuccess) {
                    //if ajax call success
                    $.fancybox({
                        "href": "#dialog-send-email",
                        "overlayShow": true,
                        "showCloseButton": true,
                        "hideOnOverlayClick": false,
                        "enableEscapeButton": false,
                        "onStart": function () {
                            var mailSent = $("#email-sent");
                            mailSent.html(mailSent.html().replace(/\{0\}/, "<b class='etc-login-intercept-mailto'>" + email + "</b>"));
                        }
                    });
                } else if (response && !response.IsSuccess) {
                    if (response.ErrorCode == 401) {
                        $("#etc-login-error-captcha").show();
                    }
                    else {
                        $("#etc-login-error-server-msg").html(response.ErrorMessage).show();
                    }
                }
            },
            complete: function () {
                confirmBtn.data("isLoading", false);
                $.fancybox.hideActivity();
            }

        });
    })
    //go to mail box action
    $(".etc-login-intercept-close").click(function () {
        $.fancybox.close();
    });




    //set Change language text
    var languageCode = getArgs();
    var text = $("li[data-lng='" + languageCode["lng"] + "']").html();
    $("#etc-login-languageBtn-text").html(text);

    //languange switch register event
    var languageHideTo;
    var indicator = $(".etc-login-indicator");
    $(".etc-login-languageBtn").mouseenter(function () {
        clearTimeout(languageHideTo);
        $(".etc-login-language").slideDown("fast");
        indicator.addClass("is-show");
    });
    $(".etc-login-language").mouseenter(function () {
        clearTimeout(languageHideTo);
    });
    $(".etc-login-languageBtn, .etc-login-language").mouseleave(function () {
        clearTimeout(languageHideTo);
        languageHideTo = setTimeout(function () {
            $(".etc-login-language").stop().slideUp();
            indicator.removeClass("is-show");
        }, 1000);
    });
    $(".etc-login-language li").click(function (e) {
        var link = $(e.target),
                code = link.data('lng'),
                location = String(document.location),
                pos = location.lastIndexOf("#");

        if (pos >= 0) {
            location = location.substring(0, pos);
        }
        location = location.replace(/(&)?lng=[^&]*/, "").replace(/\?$|(\?)&/, "$1");
        document.location = location + (/\?/.test(location) ? "&" : "?") + "lng=" + code;
    });
});