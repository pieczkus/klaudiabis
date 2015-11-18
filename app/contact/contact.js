angular.module('contactModule', [])
    .factory('ContactService', function ($http, $q) {
        return {
            sendMessage: function (message) {
                return $http.post('api/contact', message);
            }
        };
    }).controller('ContactCtrl', function ($scope, ContactService) {

        $scope.sendingMessage = false;
        $scope.error = false;
        $scope.success = false;

        $scope.send = function () {
            if ($scope.contactForm.$valid) {
                $scope.sendingMessage = true;
                $scope.error = false;
                $scope.success = false;
                ContactService.sendMessage($scope.message).then(function () {
                    Materialize.toast("Message sent :) ", 5000, 'green darken-1');
                    $scope.success = true;
                    $scope.message.name = "";
                    $scope.message.email = "";
                    $scope.message.subject = "";
                    $scope.message.body = "";
                    $scope.sendingMessage = false;
                }, function () {
                    Materialize.toast("Message sending not so good ;( ", 5000, 'red darken-1');
                    $scope.error = true;
                    $scope.sendingMessage = false;
                })
            }
        }
    });