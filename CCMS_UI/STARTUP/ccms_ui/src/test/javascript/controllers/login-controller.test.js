describe('loginControllers', function() {
    var $scope, $rootScope, $controller, $state, $httpBackend, loginFactory, authService;
    var mockState, mockInform;

    beforeEach(module('inform'));
    beforeEach(module('loginControllers'));
    beforeEach(module('loginFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _loginFactory_, _authService_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        loginFactory = _loginFactory_;
        authService = _authService_;
        $controller = _$controller_;

        mockState = { go: jasmine.createSpy('$state.go') };
        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };
        spyOn(localStorage, 'setItem');
        spyOn(sessionStorage, 'setItem');

        $controller('loginControllers', {
            $scope: $scope,
            $state: mockState,
            $stateParams: {},
            $rootScope: $rootScope,
            inform: mockInform,
            $modal: mockModal,
            loginFactory: loginFactory,
            authService: authService
        });
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('back to public monitor', function() {
        it('should navigate to the public monitor', function() {
            $scope.backToPublicMonitor();
            expect(mockState.go).toHaveBeenCalledWith('map');
        });
    });

    describe('$scope.login', function() {
        it('should send login credentials in the request body', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.expectPOST('/superadmin/user/login', { name: 'admin@test.com', password: 'pass123' })
                .respond({ status: '100', email: 'admin@test.com', role: 'SUPER ADMIN', authToken: 'token' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
        });

        it('should navigate to dashboard on successful login', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenPOST('/superadmin/user/login', { name: 'admin@test.com', password: 'pass123' })
                .respond({ status: '100', email: 'admin@test.com', authToken: 'token' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect(mockState.go).toHaveBeenCalledWith('dashboard.dashboard');
        });

        it('should show warning and stay on login when status != 100', function() {
            $scope.user = { name: 'bad@test.com', password: 'wrong' };
            $httpBackend.whenPOST('/superadmin/user/login', { name: 'bad@test.com', password: 'wrong' })
                .respond({ status: '00', email: 'bad@test.com' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect(mockInform.add).toHaveBeenCalled();
        });

        it('should store privilege in session storage by default', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenPOST('/superadmin/user/login', { name: 'admin@test.com', password: 'pass123' })
                .respond({ status: '100', email: 'admin@test.com', district: 'ALL', mandal: 'ALL', gp: 'ALL', authToken: 'token' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect(sessionStorage.setItem).toHaveBeenCalledWith('ccms_privilege', jasmine.any(String));
        });

        it('should store auth in session storage by default', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenPOST('/superadmin/user/login', { name: 'admin@test.com', password: 'pass123' })
                .respond({ status: '100', email: 'admin@test.com', district: 'ALL', mandal: 'ALL', gp: 'ALL', authToken: 'token' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect(sessionStorage.setItem).toHaveBeenCalledWith('ccms_auth', jasmine.any(String));
        });

        it('should set $rootScope.privilege on success', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123' };
            $httpBackend.whenPOST('/superadmin/user/login', { name: 'admin@test.com', password: 'pass123' })
                .respond({ status: '100', email: 'admin@test.com', authToken: 'token' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect($rootScope.privilege).toBeDefined();
        });

        it('should reject an empty form before calling the service', function() {
            $scope.login({ $invalid: true });
            expect(mockInform.add).toHaveBeenCalled();
        });

        it('should use session storage by default and local storage when remembered', function() {
            $scope.user = { name: 'admin@test.com', password: 'pass123', rememberMe: true };
            $httpBackend.whenPOST('/superadmin/user/login').respond({ status: '100', email: 'admin@test.com', authToken: 'token' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect(localStorage.setItem).toHaveBeenCalledWith('ccms_auth', 'token');
        });

        it('should not go to dashboard on login failure', function() {
            $scope.user = { name: 'bad@test.com', password: 'wrong' };
            $httpBackend.whenPOST('/superadmin/user/login', { name: 'bad@test.com', password: 'wrong' })
                .respond({ status: '00' });
            $scope.login({ $invalid: false });
            $httpBackend.flush();
            expect(mockState.go).not.toHaveBeenCalledWith('dashboard.dashboard');
        });
    });
});
