angular.module('klaudiabis', ['ui.router', 'pascalprecht.translate', 'ngAnimate', 'navigationModule', 'languageModule', 'productsModule', 'contactModule', 'angular-google-analytics'])
    .config(function (AnalyticsProvider) {
        AnalyticsProvider.setAccount('UA-54287525-1');
        AnalyticsProvider.trackPages(false);
        AnalyticsProvider.useAnalytics(true);
    })
    .controller('AppCtrl', function HomeCtrl($scope, $state, Analytics) {
        $state.go('home');
        $scope.$state = $state;
        Analytics.trackPage('/');
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