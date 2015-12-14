/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

'use strict';

angular.module('conferenceApp.directives', [])
    .directive('uploadBox', function() {
      return {
        restrict: 'A',
        scope: {
          uploadUrl: '=uploadUrl',
          onComplete: '&onComplete'
        },
        templateUrl: 'partials/uploadBox.html',
        link: function (scope, element, attrs) {
          element.filedrop({
            paramname: 'image',
            maxfiles: 1,
            maxfilesize: 10,
            drop: function() {
              this.url = scope.uploadUrl;
            },
            uploadFinished: function(i, file, response) {
              scope.uploadStarted = false;
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
;