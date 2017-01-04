(function(angular) {
    'use strict';
    var app = angular.module('FileManagerSiteApp');

    app.directive('angularFilemanagerSite', ['$parse', 'fileManagerSiteConfig', function($parse, fileManagerSiteConfig) {
        return {
            restrict: 'EA',
            templateUrl: fileManagerSiteConfig.tplPath + '/main.html'
        };
    }]);


})(angular);
