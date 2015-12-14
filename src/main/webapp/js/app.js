'use strict';

/**
 * @ngdoc object
 * @name conferenceApp
 * @requires $routeProvider
 * @requires conferenceControllers
 * @requires ui.bootstrap
 *
 * @description
 * Root app, which routes and specifies the partial html and controller depending on the url requested.
 *
 */
var app = angular.module('conferenceApp',
    ['conferenceControllers', 'ngRoute', 'ui.bootstrap']).
    config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/conference', {
                    templateUrl: '/partials/show_conferences.html',
                    controller: 'ShowConferenceCtrl'
                }).
                when('/job', {
                    templateUrl: '/partials/show_jobs.html',
                    controller: 'ShowJobCtrl'
                }).                
                when('/conference/create', {
                    templateUrl: '/partials/create_conferences.html',
                    controller: 'CreateConferenceCtrl'
                }).
                when('/conference/detail/:websafeConferenceKey', {
                    templateUrl: '/partials/conference_detail.html',
                    controller: 'ConferenceDetailCtrl'
                }).
                when('/job/detail/:websafeJobKey', {
                    templateUrl: '/partials/job_detail.html',
                    controller: 'JobDetailCtrl'
                }).                   
                when('/profile', {
                    templateUrl: '/partials/profile.html',
                    controller: 'MyProfileCtrl'
                }).
                when('/job/create', {
                    templateUrl: '/partials/create_jobs.html',
                    controller: 'CreateJobCtrl'
                }).
                when('/', {
                    templateUrl: '/partials/home.html'
                }).
                otherwise({
                    redirectTo: '/'
                });
        }]);

/**
 * @ngdoc filter
 * @name startFrom
 *
 * @description
 * A filter that extracts an array from the specific index.
 *
 */
app.filter('startFrom', function () {
    /**
     * Extracts an array from the specific index.
     *
     * @param {Array} data
     * @param {Integer} start
     * @returns {Array|*}
     */
    var filter = function (data, start) {
        return data.slice(start);
    }
    return filter;
});


/**
 * @ngdoc constant
 * @name HTTP_ERRORS
 *
 * @description
 * Holds the constants that represent HTTP error codes.
 *
 */
app.constant('HTTP_ERRORS', {
    'UNAUTHORIZED': 401
});


/**
 * @ngdoc service
 * @name oauth2Provider
 *
 * @description
 * Service that holds the OAuth2 information shared across all the pages.
 *
 */
app.factory('oauth2Provider', function ($modal) {
    var oauth2Provider = {
        CLIENT_ID: '127228821153-e65804hqbl98dqpdcpf0gn5sl5b4ke0m.apps.googleusercontent.com',
        SCOPES: 'https://www.googleapis.com/auth/userinfo.email profile',
        signedIn: false
    };

    /**
     * Calls the OAuth2 authentication method.
     */
    oauth2Provider.signIn = function (callback) {
        gapi.auth.signIn({
            'clientid': oauth2Provider.CLIENT_ID,
            'cookiepolicy': 'single_host_origin',
            'accesstype': 'online',
            'approveprompt': 'auto',
            'scope': oauth2Provider.SCOPES,
            'callback': callback
        });
    };

    /**
     * Logs out the user.
     */
    oauth2Provider.signOut = function () {
        gapi.auth.signOut();
        // Explicitly set the invalid access token in order to make the API calls fail.
        gapi.auth.setToken({access_token: ''})
        oauth2Provider.signedIn = false;
    };

    /**
     * Shows the modal with Google+ sign in button.
     *
     * @returns {*|Window}
     */
    oauth2Provider.showLoginModal = function() {
        var modalInstance = $modal.open({
            templateUrl: '/partials/login.modal.html',
            controller: 'OAuth2LoginModalCtrl'
        });
        return modalInstance;
    };

    return oauth2Provider;
});


app.directive('photo', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        item: '=',
        deletePhoto: '&deletePhoto'
      },
      templateUrl: 'partials/photo.html',
      link: function (scope, element, attrs) {
        element.find('.voteButton')
            .click(function(evt) {
              if(scope.item.canVote && !scope.item.voted) {
                var voteButton = angular.element(evt.target)
                scope.$apply(function() {
                  voteButton.unbind('click');
                  scope.item.numVotes = scope.item.numVotes + 1;
                  scope.item.voted = true;
                  voteButton.focus();
                  scope.item.voteClass.push('disable');
                });
                PhotoHuntApi.votePhoto(scope.item.id)
                    .then(function(response) {});
              }
            });
      
        element.find('.remove')
            .click(function() {
              if (scope.item.canDelete) {
                scope.deletePhoto({photoId: scope.item.id});
              }
            });
        
        var options = {
          'clientid': Conf.clientId,
          'contenturl': scope.item.photoContentUrl,
          'contentdeeplinkid': '/?id=' + scope.item.id,
          'prefilltext': 'What do you think?  Does this image embody \'' +
              scope.item.themeDisplayName + '\'? #photohunt',
          'calltoactionlabel': 'VOTE',
          'calltoactionurl': scope.item.voteCtaUrl,
          'calltoactiondeeplinkid': '/?id=' + scope.item.id + '&action=VOTE',
          'requestvisibleactions': Conf.requestvisibleactions,
          'scope': Conf.scopes,
          'cookiepolicy': Conf.cookiepolicy
        }
        gapi.interactivepost.render(
            element.find('.toolbar button').get(0), options);
      }
    }
  })

app.directive('uploadBox', function() {
      return {
        restrict: 'A',
        scope: {
          uploadUrl: '=uploadUrl',
          onComplete: '&onComplete',
        },
        templateUrl: 'partials/uploadBox.html',
        link: function (scope, element, attrs) {
          element.filedrop({
            paramname: 'image',
            websafeJobKey: 'ahNtaW5lcmFsLWNlbGwtMTEyMjEychcLEgdQcm9maWxlIgEwDAsSA0pvYhgBDA',
            maxfiles: 1,
            maxfilesize: 10,
            drop: function() {
            	// Debug
            	console.log(scope.uploadUrl);
            	// End debug
              this.url = scope.uploadUrl;
            },
            uploadFinished: function(i, file, response) {
              scope.uploadStarted = false;
          	// Debug
          	console.log('Upload finished ' + scope.uploadUrl);
          	// End debug
            this.url = scope.uploadUrl;
              scope.onComplete({photo: response});
            },
            error: function(err, file) {
              switch (err) {
                case 'BrowserNotSupported':
                  alert('Your browser does not support HTML5 file uploads!');
                  break;
                case 'TooManyFiles':
                  alert('Too many files!');
                  break;
                case 'FileTooLarge':
                  alert(file.name + ' is too large! Please upload files up to 10mb.');
                  break;
                default:
                  break;
              }
            },
            // Called before each upload is started
            beforeEach: function(file) {
              if (!file.type.match(/^image\//)) {
                alert('Only images are allowed!');
                // Returning false will cause the
                // file to be rejected
                return false;
              }
            },
            uploadStarted: function(i, file, len) {
              scope.uploadStarted = true;
              scope.uploadProgress = 0;
              var reader = new FileReader();
              reader.onload = function(e) {
                // e.target.result holds the DataURL which
                // can be used as a source of the image:
                scope.$apply(function() {
                  scope.imagePreview = e.target.result;
                });
              };
              // Reading the file as a DataURL. When finished,
              // this will trigger the onload function above:
              reader.readAsDataURL(file);
            },
            progressUpdated: function (i, file, progress) {
              scope.uploadProgress = progress;
            }
          });
        }
      }
    })
    
    
 app.filter('profilePicture', function() {
     return function(profilePicUrl, size) {
         if(profilePicUrl) {
           var clean = profilePicUrl.replace(/\?sz=(\d)*$/, ''); 
           return clean + '?sz=' + size;
         } else {
           return '';
         }
       };
     })     
     
     
     