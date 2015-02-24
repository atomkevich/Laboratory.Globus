var app = angular.module("app", ["ngResource", "ngRoute"])
	.config(["$routeProvider", function($routeProvider) {
		return $routeProvider.when("/", {
			templateUrl: "/views/employees",
			controller: "EmployeeCtrl"
		}).when("/pods", {
			templateUrl: "/views/pods",
			controller: "PODCtrl"
	    }).when("/employee/newEmployee", {
			templateUrl: "/views/newEmployee",
			controller: "EmployeeCardCtrl"
	    }).when("/employee:id", {
            templateUrl: "/views/employee",
            controller: "EmployeeCardCtrl"
        }).when("/pod/newPOD", {
			templateUrl: "/views/newPOD",
			controller: "PODCardCtrl"
		}).when("/logout", {
            controller: "LogoutCtrl"
        }).otherwise({
			redirectTo: "/login"
		});
	}
	]).config([
	"$locationProvider", function($locationProvider) {
		return $locationProvider.html5Mode({
			enabled: true,
			requireBase: false
		}).hashPrefix("!"); // enable the new HTML5 routing and history API
		// return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and history API
	}
]);

app.controller("LogoutCtrl", ["$location", "$scope", "$resource" function($location, $scope, $resource) {
    $location.path('/logout');
    $scope.apply();
    $resource("/logout").query();
}]);

app.controller("PODCtrl", ["$scope", "$resource", "$location", function($scope, $resource, $location) {
	// the very sweet go function is inherited by all other controllers
	$scope.go = function (path) {
		$location.path(path);
	};
	var pods = $resource("/pods");
	pods.query().$promise.then(
		function( value ){
			$scope.pods = value;
		},
		function( error ){
			console.log("error")
		}
	);
}]);



// the list controller
app.controller("EmployeeCtrl", ["$scope", "$resource", "$routeParams", function($scope, $resource, $routeParams) {
	var employees = $resource("/employees");
	 employees.query().$promise.then(
		function( value ){
			$scope.employees = value;
		},
		function( error ){
			console.log("error")
		}
	);



    $resource("/current").get().$promise.then(
      function(value) {
          $scope.currentUser = value;
      }
    );
    $scope.resetEmployeeFilter = function() {
        var employees = $resource("/employees").query();
        employees.query().$promise.then(
            function( value ){
                $scope.employees = value;
            },
            function( error ){
                console.log("error")
            });
    }
	$scope.filterEmployees = function() {
		var findEmployees = $resource("/employees?" + $scope.filter.param + "=" + $scope.filter.value);
		findEmployees.query().$promise.then(
			function( value ){
				$scope.employees = value;
                $scope.$apply();
			},
			function( error ){
				console.log("error")
			}
		)
	};

	$scope.editEmployee = function(empl) {
		empl._edit = true;
		console.log("edit")
	}

	$scope.deleteEmployee= function(id) {
		 $resource("/employees/" + id).delete(); // a RESTful-capable resource object

	}
	$scope.submitEmployee = function(empl) {
		console.log("submit")
        $resource("/employees/profile").update(empl); // $scope.celebrity comes from the detailForm in public/html/detail.html
        empl._edit = false;
	}


}]);



app.controller("EmployeeCardCtrl", ["$http", "$timeout", "$location", "$window", "$scope", "$resource", "$routeParams",
                            function($http, $timeout, $location, $window, $scope, $resource) {
	$scope.go = function (path) {
		$location.path(path);
	};

	$scope.createEmployees = function() {
        $http({
            url:'/employees',
            method : 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: $scope.employee
        }).success(function(){
            $location.path("/");
            $scope.apply();
        }).error(function(err){
            $scope.errorMessage = err;
        });

	}
}]);

app.controller("PODCardCtrl", ["$http", "$routeParams", "$timeout", "$location", "$scope", "$resource", function($http, $timeout, $location, $scope, $resource) {
	$scope.go = function (path) {
		$location.path(path);
	};

	$scope.createPOD = function() {
		$resource("/pods").save($scope.pod);
		$timeout(function() { $scope.go('/pods'); });
	}
}]);