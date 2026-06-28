describe('loginControllers', function() {
    var $scope, $rootScope, $controller, $state, $httpBackend, loginFactory, inform, $window;
    var mockState, mockInform, mockLocalStorage, mockModal, mockBase64;

    beforeEach(module('inform'));
    beforeEach(module('loginControllers'));
    beforeEach(module('loginFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _loginFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        loginFactory = _loginFactory_;
        $controller = _$controller_;

        mockState = { go: jasmine.createSpy('$state.go') };
        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };
        mockBase64 = { encode: function(s) { return btoa(s); }, decode: function(s) { return atob(s); } };

        spyOn(localStorage, 'setItem');

        $controller('loginControllers', {
            $scope: $scope,
            $state: mockState,
            $stateParams: {},
            $rootScope: $rootScope,
            inform: mockInform,
            $modal: mockModal,
            loginFactory: loginFactory,
            Base64: mockBase64
        });
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('$scope.login', function() {
        it('should call loginFactory.login_user with query params', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.expectGET('/CCMS/superadmin/user/login?name=admin@test.com&password=pass123')
                .respond({ status: '100', email: 'admin@test.com', role: 'SUPER ADMIN' });
            $scope.login();
            $httpBackend.flush();
        });

        it('should navigate to dashboard on successful login', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=admin@test.com&password=pass123')
                .respond({ status: '100', email: 'admin@test.com' });
            $scope.login();
            $httpBackend.flush();
            expect(mockState.go).toHaveBeenCalledWith('dashboard.dashboard');
        });

        it('should show warning and stay on login when status != 100', function() {
            $scope.user = { name: 'bad@test.com', password: 'wrong' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=bad@test.com&password=wrong')
                .respond({ status: '00', email: 'bad@test.com' });
            $scope.login();
            $httpBackend.flush();
            expect(mockInform.add).toHaveBeenCalled();
        });

        it('should store previlage in localStorage on success', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=admin@test.com&password=pass123')
                .respond({ status: '100', email: 'admin@test.com', dist: 'ALL', mondal: 'ALL', gp: 'ALL' });
            $scope.login();
            $httpBackend.flush();
            expect(localStorage.setItem).toHaveBeenCalledWith('ccms_previlage', jasmine.any(String));
        });

        it('should store auth in localStorage on success', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=admin@test.com&password=pass123')
                .respond({ status: '100', email: 'admin@test.com', dist: 'ALL', mondal: 'ALL', gp: 'ALL' });
            $scope.login();
            $httpBackend.flush();
            expect(localStorage.setItem).toHaveBeenCalledWith('ccms_auth', jasmine.any(String));
        });

        it('should set $rootScope.previlage on success', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=admin@test.com&password=pass123')
                .respond({ status: '100', email: 'admin@test.com' });
            $scope.login();
            $httpBackend.flush();
            expect($rootScope.previlage).toBeDefined();
        });

        it('should not go to dashboard on login failure', function() {
            $scope.user = { name: 'bad@test.com', password: 'wrong' };
            $httpBackend.whenGET('/CCMS/superadmin/user/login?name=bad@test.com&password=wrong')
                .respond({ status: '00' });
            $scope.login();
            $httpBackend.flush();
            expect(mockState.go).not.toHaveBeenCalledWith('dashboard.dashboard');
        });
    });
});
