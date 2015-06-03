angular.module('klaudiabis', ['ui.router', 'pascalprecht.translate', 'ngAnimate', 'navigationModule', 'languageModule', 'productsModule'])
    .controller('AppCtrl', function HomeCtrl($scope, $state) {
        $state.go('home');
        $scope.$state = $state;
    })
    .directive("scroll", function ($window) {
        return function (scope, element, attrs) {
            angular.element($window).bind("scroll", function () {
                if (this.pageYOffset >= 100) {
                    scope.fillNavigation = true;
                } else {
                    scope.fillNavigation = false;
                }
                scope.$apply();
            });
        };
    });